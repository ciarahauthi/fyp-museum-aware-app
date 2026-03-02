package com.stitchumsdev.fyp.core.ble

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.stitchumsdev.fyp.R
import com.stitchumsdev.fyp.core.data.repository.BeaconRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class BleScanService: Service() {
    private lateinit var scanner: BleScanner
    private val repository: BeaconRepository by inject()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val flushInterval = 15 * 60000L // 15mins

    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())

        scope.launch {
            while (isActive) {
                delay(flushInterval)
                repository.uploadClearPackets()
            }
        }

        scanner = BleScanner(this) { beacon ->
            scope.launch {
                repository.onBeacon(beacon)
            }
        }
        scanner.start()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun onDestroy() {
        super.onDestroy()

        scanner.stop()

        scope.launch { repository.uploadClearPackets() } // Final flush
        repository.endSession()

        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
    private fun createNotification(): Notification {
        val channelId = "ble_scan"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "BLE scanning",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Scanning for beacons")
            .setContentText("BLE scanning active")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }
}