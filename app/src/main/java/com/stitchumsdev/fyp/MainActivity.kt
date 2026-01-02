package com.stitchumsdev.fyp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private var scanning = false

    @RequiresApi(Build.VERSION_CODES.S)
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_SCAN
    )

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        enableEdgeToEdge()
        setContent {
            AppContent()
        }
        requestNeededPermissions()
    }

    // -------- PERMISSIONS ----------
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestNeededPermissions() {
        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 100)
        } else {
            startScanning()
        }
    }

    // Called after user accepts permissions
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            startScanning()
        } else {
            Log.e("BEACON", "!! Permissions denied — cannot scan")
        }
    }

        @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
        @RequiresApi(Build.VERSION_CODES.S)
        private fun startScanning() {
            if (scanning) return       // Prevent multiple scans
            scanning = true

            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

            if (bluetoothAdapter == null) {
                Log.e("BEACON", "!! Bluetooth not supported on this device")
                return
            }

            val scanner = bluetoothAdapter.bluetoothLeScanner

            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            Log.d("BEACON", "!! Starting BLE scan...")
            scanner.startScan(null, settings, scanCallback)
        }

        private fun stopScanning() {
            if (!scanning) return
            scanning = false

            val scanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
            scanner.stopScan(scanCallback)
        }

        fun parseIBeacon(scanRecord: ByteArray?, rssi: Int): IBeaconData? {
            if (scanRecord == null) return null

            for (i in 0 until scanRecord.size - 30) {
                // Look for iBeacon header: 4C 00 02 15
                if (scanRecord[i] == 0x4C.toByte() &&
                    scanRecord[i + 1] == 0x00.toByte() &&
                    scanRecord[i + 2] == 0x02.toByte() &&
                    scanRecord[i + 3] == 0x15.toByte()
                ) {

                    val uuidBytes = scanRecord.copyOfRange(i + 4, i + 20)

                    val uuid = String.format(
                        "%02X%02X%02X%02X-%02X%02X-%02X%02X-%02X%02X-%02X%02X%02X%02X%02X%02X",
                        *uuidBytes.toTypedArray()
                    )

                    val major = ((scanRecord[i + 20].toInt() and 0xFF) shl 8) +
                            (scanRecord[i + 21].toInt() and 0xFF)

                    val minor = ((scanRecord[i + 22].toInt() and 0xFF) shl 8) +
                            (scanRecord[i + 23].toInt() and 0xFF)

                    val txPower = scanRecord[i + 24].toInt()

                    return IBeaconData(uuid, major, minor, txPower, rssi)
                }
            }
            return null
        }

        private val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {

                val record = result.scanRecord?.bytes ?: return

                val beacon = parseIBeacon(record, result.rssi)
                if (beacon != null) {
                    Log.d(
                        "ESTIMOTE",
                        "!! UUID: ${beacon.uuid}, major: ${beacon.major}, minor: ${beacon.minor}, rssi: ${beacon.rssi}, tx pow: ${beacon.txPower}"
                    )
                    Timber.d("!! TIMBER")
                }
            }
        }

    data class IBeaconData(
        val uuid: String,
        val major: Int,
        val minor: Int,
        val txPower: Int,
        val rssi: Int
    )
}