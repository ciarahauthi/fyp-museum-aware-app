package com.stitchumsdev.fyp.core.ble

import com.stitchumsdev.fyp.core.model.IBeaconData

object IBeaconParser {
    fun parse(scanRecord: ByteArray?, rssi: Int): IBeaconData? {
        if (scanRecord == null) return null

        for (i in 0 until scanRecord.size - 30) {
            // iBeacon header: 4C 00 02 15
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
}