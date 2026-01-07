package com.stitchumsdev.fyp.core.ble

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.stitchumsdev.fyp.R

class BleScanService: Service() {
    private lateinit var scanner: BleScanner

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun onCreate() {
        super.onCreate()
        scanner = BleScanner(this) { beacon ->
            //ToDo send api call here
        }
        startForeground(1, createNotification())
        scanner.start()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun onDestroy() {
        scanner.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
        super.onTaskRemoved(rootIntent)
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