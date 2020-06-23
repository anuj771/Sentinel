@file:Suppress("UNREACHABLE_CODE")

package com.sentinel.util
import android.content.IntentFilter

import java.io.UnsupportedEncodingException
import kotlin.experimental.and

object AppConstant {
    val REQUEST_LOCATION_PERMISSION = 1
    val MY_MARSHMELLO_PERMISSION = 2
    val REQUEST_ENABLE_BLUETOOTH = 3
    val REQUEST_ENABLE_LOCATION = 4
    val REQUEST_PHONE_STATE_PERMISSION = 5

    val service_uuid = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E"
    val notify_char_uuid = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"
    val write_char_uuid = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
    val desc_uuid = "00002902-0000-1000-8000-00805f9b34fb"

    val ACTION_DEVICE_DISCONNECTED = "com.scheduler.ACTION_DEVICE_DISCONNECTED"
    val ACTION_DEVICE_CONNECTED = "com.scheduler.ACTION_DEVICE_CONNECTED"
    val ACTION_CHARACTERISTIC_CHANGED = "com.scheduler.ACTION_CHARACTERISTIC_CHANGED"
    val ACTION_CHARACTERISTIC_WRITE = "com.scheduler.ACTION_CHARACTERISTIC_WRITE"

    fun makeIntentFilter(): IntentFilter {
        val filter = IntentFilter()
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY - 1
        filter.addAction(ACTION_DEVICE_CONNECTED)
        filter.addAction(ACTION_DEVICE_DISCONNECTED)
        filter.addAction(ACTION_CHARACTERISTIC_WRITE)
        filter.addAction(ACTION_CHARACTERISTIC_CHANGED)
        return filter
    }

    fun convertStringToByte(text: String): ByteArray {
        try {
            return text.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return ByteArray(0)
    }

     val HEX_CHARS = "0123456789ABCDEF".toCharArray()
    fun byteArrytoHex(byteArry:ByteArray) : String{
        val result = StringBuffer()

        for (i in 0 until byteArry.size step 1) {
            val octet = byteArry[i].toInt()
            val firstIndex = (octet and 0xF0).ushr(4)
            val secondIndex = octet and 0x0F
            result.append(HEX_CHARS[firstIndex])
            result.append(HEX_CHARS[secondIndex])
        }

        return result.toString()
    }

     val HEX_CHARS_STRING = "0123456789ABCDEF"
    fun hexStringToByteArray(string:String) : ByteArray {
        val result = ByteArray(string.length / 2)
        for (i in 0 until string.length step 2) {
            val firstIndex = HEX_CHARS_STRING.indexOf(string[i]);
            val secondIndex = HEX_CHARS_STRING.indexOf(string[i + 1]);

            val octet = firstIndex.shl(4).or(secondIndex)
            result.set(i.shr(1), octet.toByte())
        }
        return result
    }

}
