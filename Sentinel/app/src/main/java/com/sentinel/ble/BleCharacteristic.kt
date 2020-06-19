package com.sentinel.ble
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Arrays
import java.util.UUID

import com.sentinel.R
import com.sentinel.activity.MainActivity
import com.sentinel.ble.BleDeviceActor.Companion.bleWriteCharacteristic
import com.sentinel.util.AppConstant
import com.sentinel.util.CheckSelfPermission

object BleCharacteristic {
    var MAX_TRANS_COUNT = 20

  /*  fun writeDataToDevice(context: Context, randomData: ByteArray) {
        var randomData = randomData
        Log.d("ble==> ", "chalangeDATA hax: " + AppConstant.byteArrayToHexString(randomData))
        randomData = removefirstvaluefromByte(randomData)
        //        Log.d("ble==> ", "chalangeDATA new hax: " + AppConstant.byteArrayToHexString(randomData));
        val encryptedChalangeData = encryptData(randomData, context)
        Log.d("ble==> ", "chalangeDATA encypted hax: " + AppConstant.byteArrayToHexString(encryptedChalangeData))
        //        Log.d("ble==> ", "chalangeDATA encypted: " + Arrays.toString(encryptedChalangeData));
        val binArray = sendPacket(context, encryptedChalangeData)
        Log.d("ble==> ", "bytePacketForSend: " + AppConstant.byteArrayToHexString(binArray))

        if (binArray == null || binArray.size == 0) {
            return
        }
        var count = 0
        var DataArray = ByteArray(MAX_TRANS_COUNT)
        if (binArray.size < MAX_TRANS_COUNT) {
            DataArray = ByteArray(binArray.size)
        }
        var i: Int
        i = 0
        while (i < binArray.size) {
            DataArray[count] = binArray[i]
            count += 1
            if (count == MAX_TRANS_COUNT) {
                bleWriteCharacteristic(DataArray)
                count = 0
                if (binArray.size - (i + 1) < MAX_TRANS_COUNT) {
                    DataArray = ByteArray(binArray.size - (i + 1))
                }
            } else {
                if (i == binArray.size - 1) {
                    bleWriteCharacteristic(DataArray)
                    count = 0
                }
            }
            i++
        }
    }


    fun sendPacket(context: Context, chalangedataencrpted: ByteArray): ByteArray? {
        val mBluetoothGatt = BleDeviceActor.getmBluetoothGatt()
        if (canReadWrite(context, mBluetoothGatt)) {
            val deviceID = SharedPref.getValue(context, SharedPref.PREF_DEVICE_ID, "")
            Log.d("ble==> ", "deviceID imei: $deviceID")
            val deviceIdData = AppConstant.convertStringToByte(deviceID.toUpperCase())
            val to_send = ByteArrayOutputStream()
            try {
                to_send.write(chalangedataencrpted)
                to_send.write(deviceIdData)
                to_send.write(0x7d)
                to_send.write(0x0a)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return to_send.toByteArray()
        } else {
            val intent = Intent(AppConstant.ACTION_DEVICE_DISCONNECTED)
            context.sendBroadcast(intent)
            return null
        }
    }

    fun removefirstvaluefromByte(chalangeData: ByteArray): ByteArray {
        return Arrays.copyOfRange(chalangeData, 1, chalangeData.size)
    }*/

    fun enableNotifyChar(context: Context) {
        val mBluetoothGatt = BleDeviceActor.getmBluetoothGatt()
        if (!canReadWrite(context, mBluetoothGatt)) {
            return
        }
        val characteristic = mBluetoothGatt
            ?.getService(UUID.fromString(AppConstant.service_uuid))
            ?.getCharacteristic(UUID.fromString(AppConstant.notify_char_uuid))

        val descriptor = characteristic?.getDescriptor(UUID.fromString(AppConstant.desc_uuid))
        descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        val isWrite = mBluetoothGatt?.writeDescriptor(descriptor)
        mBluetoothGatt?.setCharacteristicNotification(characteristic, true)
        Log.d("ble==> ", "enableNotifyChar: $isWrite")
    }


    fun canReadWrite(context: Context, mBluetoothGatt: BluetoothGatt?): Boolean {
        if (!CheckSelfPermission.isBluetoothOn(context)) {
            Toast.makeText(context, context.getString(R.string.enable_bluetooth), Toast.LENGTH_SHORT).show()
            return false
        } else if (!BleDeviceActor.isIsConnected) {
            Toast.makeText(context, context.getString(R.string.device_disconnected), Toast.LENGTH_SHORT).show()
            return false
        } else return if (mBluetoothGatt == null) {
            false
        } else {
            true
        }
    }
}
