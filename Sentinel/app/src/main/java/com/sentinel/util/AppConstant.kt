@file:Suppress("UNREACHABLE_CODE")

package com.sentinel.util

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.IntentFilter
import com.sentinel.R

import java.io.UnsupportedEncodingException
import java.lang.Exception

object AppConstant {
    val REQUEST_LOCATION_PERMISSION = 1
    val MY_MARSHMELLO_PERMISSION = 2
    val REQUEST_ENABLE_BLUETOOTH = 3
    val REQUEST_ENABLE_LOCATION = 4

    val READ_COMMAND = 0
    val WRITE_COMMAND = 1
    val ERASE_COMMAND = 2
    val SET_RTC_DATE_COMMAND = 3
    val START_DATE_COMMAND = 4
    val SET_DURATION_COMMAND = 5
    val FAN_COMMAND = 6
    val GO_TO_POSITION_COMMAND = 7
    val GET_POSITION_COMMAND = 8
    val RESET_COMMAND = 0X0a

    val RESPONSE_SUCCESS = 0
    val RESPONSE_PARSING_ERROR = 1
    val RESPONSE_UNKNOWN_ERROR = 2
    val RESPONSE_ILLEGAL_RANGE_ERROR = 3
    val RESPONSE_CRC_MISMATCH_ERROR = 4

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
    fun byteArrytoHex(byteArry: ByteArray): String {
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
    fun hexStringToByteArray(string: String): ByteArray {
        val result = ByteArray(string.length / 2)
        for (i in 0 until string.length step 2) {
            val firstIndex = HEX_CHARS_STRING.indexOf(string[i]);
            val secondIndex = HEX_CHARS_STRING.indexOf(string[i + 1]);

            val octet = firstIndex.shl(4).or(secondIndex)
            result.set(i.shr(1), octet.toByte())
        }
        return result
    }

    var pDialog: ProgressDialog? = null
    fun openProgressDialog(context: Context) {
        dismissProgrssDialog()
        pDialog = ProgressDialog(context)
        pDialog!!.setCancelable(false)
        pDialog!!.setMessage(context.getString(R.string.please_wait))
        pDialog!!.show()
    }

    fun dismissProgrssDialog() {
        try {
            if (pDialog != null && pDialog!!.isShowing) {
                pDialog!!.dismiss()
            }
        } catch (e: Exception) {
        }

    }

    fun getDeviceResponseMsg(response: Int, context: Context): String {
        when (response) {
            AppConstant.RESPONSE_SUCCESS -> return context.getString(R.string.response_success)
            AppConstant.RESPONSE_PARSING_ERROR -> return context.getString(R.string.response_parse_error)
            AppConstant.RESPONSE_UNKNOWN_ERROR -> return context.getString(R.string.response_unkonwn_error)
            AppConstant.RESPONSE_ILLEGAL_RANGE_ERROR -> return context.getString(R.string.response_illegel_range_error)
            AppConstant.RESPONSE_CRC_MISMATCH_ERROR -> return context.getString(R.string.response_crc_mismatch_error)
            else -> return context.getString(R.string.response_unkonwn_error)
        }
    }

    var responseDialog: AlertDialog? = null
    fun ShowResponseDialog(context: Context, response: ByteArray) {
        try {
            if (responseDialog != null) {
                if (responseDialog!!.isShowing) {
                    responseDialog!!.dismiss()
                }
            }
        } catch (e: Exception) {
        }

        responseDialog = AlertDialog.Builder(context)
            .setTitle(context.resources.getString(com.sentinel.R.string.device_response))
            .setMessage(AppConstant.byteArrytoHex(response)+"\n"+getDeviceResponseMsg(response[0].toInt(),context))
            .setCancelable(false)
            .setPositiveButton("ok") { dialog, whichButton ->
                responseDialog!!.dismiss()
            }.show()
    }
}


