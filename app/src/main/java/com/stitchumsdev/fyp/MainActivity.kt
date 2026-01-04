package com.stitchumsdev.fyp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.stitchumsdev.fyp.core.ble.BleScanService
import timber.log.Timber

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    private val permissions = arrayOf(
        Manifest.permission.FOREGROUND_SERVICE_LOCATION,
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

    // PERMISSIONS
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestNeededPermissions() {
        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 100)
        } else {
            val intent = Intent(this, BleScanService::class.java)
            startForegroundService(intent)
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
            val intent = Intent(this, BleScanService::class.java)
            startForegroundService(intent)
        } else {
            Timber.d("!! Permissions denied")
        }
    }
}