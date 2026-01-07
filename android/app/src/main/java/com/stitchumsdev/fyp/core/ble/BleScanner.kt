package com.stitchumsdev.fyp.core.ble

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.stitchumsdev.fyp.core.model.IBeaconData
import timber.log.Timber

class BleScanner(
    private val context: Context,
    private val onBeacon: (IBeaconData) -> Unit
) {
    private val btManager by lazy { context.getSystemService(BluetoothManager::class.java) }
    private val scanner get() = btManager?.adapter?.bluetoothLeScanner
    private var scanning = false

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    @RequiresApi(Build.VERSION_CODES.S)
    fun start() {
        if (scanning) return

        val s = scanner ?: run {
            Timber.d("!! Bluetooth not supported on this device / scanner unavailable")
            return
        }

        try {
            scanning = true
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            Timber.d("!! Starting scan...")
            s.startScan(null, settings, scanCallback)
        } catch (e: Exception) {
            scanning = false
            Timber.d("!! Init scan failed: $e")
        }
    }

    // Stop function as service is not auto killed after closing app
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stop() {
        if (!scanning) return
        scanning = false

        val s = scanner ?: return
        s.stopScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanFailed(e: Int) {
            scanning = false
            Timber.e("!! Scan failed: $e")
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {

            val record = result.scanRecord?.bytes ?: return

            val beacon = IBeaconParser.parse(record, result.rssi)
            if (beacon != null) {
                // ToDo make this UUID dynamic
                if (!beacon.uuid.equals("B9407F30-F5F8-466E-AFF9-25556B57FE6D", ignoreCase = true)) return
                Timber.d("!! UUID: ${beacon.uuid}, major: ${beacon.major}, minor: ${beacon.minor}, rssi: ${beacon.rssi}, tx pow: ${beacon.txPower}")
            }
        }
    }
}