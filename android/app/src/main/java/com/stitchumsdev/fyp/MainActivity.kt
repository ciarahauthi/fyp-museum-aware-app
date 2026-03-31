package com.stitchumsdev.fyp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.stitchumsdev.fyp.core.ble.BleScanService
import com.stitchumsdev.fyp.core.ui.theme.FypTheme
import com.stitchumsdev.fyp.feature.splash.PermissionScreen
import timber.log.Timber

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    private val permissions = arrayOf(
        Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_SCAN
    )

    private var hasPermissions by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        enableEdgeToEdge()

        hasPermissions = hasAllPermissions()

        setContent {
            FypTheme {
                if (hasPermissions) {
                    AppContent()
                } else {
                    PermissionScreen(
                        onRequest = { requestNeededPermissions() }
                    )
                }
            }
        }

        // If permissions, start service now otherwise request permissions
        if (hasPermissions) startBleService()
        else ActivityCompat.requestPermissions(this, missingPermissions().toTypedArray(), 100)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        // Case in which the user granted permissions in the settings app and returned to app
        if (!hasPermissions && hasAllPermissions()) {
            hasPermissions = true
            startBleService()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun hasAllPermissions() = missingPermissions().isEmpty()

    @RequiresApi(Build.VERSION_CODES.S)
    private fun missingPermissions() = permissions.filter {
        ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
    }

    // Called by the "Grant Permissions" button
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestNeededPermissions() {
        val missing = missingPermissions()

        if (missing.isEmpty()) {
            hasPermissions = true
            startBleService()
            return
        }

        // shouldShowRequestPermissionRationale is false after "Don't ask again". Open settings instead
        val permanentlyDenied = missing.any {
            !ActivityCompat.shouldShowRequestPermissionRationale(this, it)
        }

        if (permanentlyDenied) {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            })
        } else {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 100)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startBleService() {
        val intent = Intent(this, BleScanService::class.java)
        startForegroundService(intent)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val granted = requestCode == 100 &&
                grantResults.isNotEmpty() &&
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }

        if (granted) {
            hasPermissions = true
            startBleService()
        } else {
            hasPermissions = false
            Timber.d("!! Permissions denied")
        }
    }
}