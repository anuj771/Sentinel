package com.sentinel.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.sentinel.util.AppConstant

import java.util.Arrays
import java.util.UUID

class BleDeviceActor(mContext: Context) : Runnable {
    private var cContext: Context? = null

    init {
        this.cContext = mContext
    }

    override fun run() {
        if (mBluetoothDevice == null || mContext == null) {
            return
        }
        try {
            if (mBluetoothGatt != null) {
                mBluetoothGatt!!.close()
                mBluetoothGatt = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt =
                mBluetoothDevice!!.connectGatt(mContext, false, mGattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            mBluetoothGatt = mBluetoothDevice!!.connectGatt(mContext, false, mGattCallback)
        }
    }

    fun connectToDevice(bluetoothDevice: BluetoothDevice, cContext: Context) {
        mContext = cContext
        mBluetoothDevice = bluetoothDevice
        stopThread()
        startThread()
    }

    fun startThread() {
        thread = Thread(this)
        thread!!.start()
    }

    companion object {
        @JvmStatic lateinit var mContext: Context
        private var mBluetoothGatt: BluetoothGatt? = null
        private var mBluetoothDevice: BluetoothDevice? = null
        private var thread: Thread? = null
        var isIsConnected = false
            private set

        fun getmBluetoothGatt(): BluetoothGatt? {
            return mBluetoothGatt
        }

        fun disconnectDevice() {
            try {
                if (mBluetoothGatt != null && isIsConnected) {
                    mBluetoothGatt!!.disconnect()
                    mBluetoothGatt!!.close()
                    isIsConnected = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


        var mGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                Log.d("ble==> ", "onConnectionStateChange: $newState")
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    isIsConnected = false
                    mBluetoothGatt = null
                    broadcastUpdate(AppConstant.ACTION_DEVICE_DISCONNECTED)
                    stopThread()
                } else {
                    isIsConnected = false
                    mBluetoothGatt = null
                    broadcastUpdate(AppConstant.ACTION_DEVICE_DISCONNECTED)
                    stopThread()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)
                Log.d("ble==> ", "onServicesDiscovered")
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (gatt != null) {
                        isIsConnected = true
                        mBluetoothGatt = gatt
                        BleCharacteristic.enableNotifyChar(mContext!!)
                        Log.d("ble==> ", "onServicesDiscovered: success")
                    }
                }
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                super.onCharacteristicRead(gatt, characteristic, status)
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                super.onCharacteristicWrite(gatt, characteristic, status)
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic?) {
                super.onCharacteristicChanged(gatt, characteristic)
                Log.d("ble==> ", "onCharacteristicChanged: " + Arrays.toString(characteristic!!.value))
                if (characteristic != null) {
                    val data = characteristic.value
                    if (data != null) {
                        broadcastUpdate(AppConstant.ACTION_CHARACTERISTIC_CHANGED, data)
                    }
                }
            }

            override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
                super.onDescriptorRead(gatt, descriptor, status)
            }

            override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
                super.onDescriptorWrite(gatt, descriptor, status)
                broadcastUpdate(AppConstant.ACTION_DEVICE_CONNECTED)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(
                        "ble==> ",
                        "onDescriptorWrite: success " + descriptor.characteristic.uuid.toString() + "   value: " + descriptor.value[0]
                    )
                } else {
                    Log.e("ble==> ", "onDescriptorWrite: false status: $status")
                }
            }
        }

        fun stopThread() {
            if (thread != null) {
                val tempThread = thread
                thread = null
                tempThread!!.interrupt()
            }
        }

        fun broadcastUpdate(action: String) {
            val intent = Intent(action)
            mContext!!.sendBroadcast(intent)
        }

        private fun broadcastUpdate(action: String, data: String) {
            val intent = Intent(action)
            intent.putExtra("data", data)
            mContext!!.sendBroadcast(intent)
        }

        private fun broadcastUpdate(action: String, data: Int) {
            val intent = Intent(action)
            intent.putExtra("data", data)
            mContext!!.sendBroadcast(intent)
        }

        private fun broadcastUpdate(action: String, data: ByteArray) {
            val intent = Intent(action)
            intent.putExtra("data", data)
            mContext!!.sendBroadcast(intent)
        }

        fun bleWriteCharacteristic(byteValue: ByteArray) {
            val mBluetoothGatt = getmBluetoothGatt()
            val characteristic = mBluetoothGatt!!
                .getService(UUID.fromString(AppConstant.service_uuid))
                .getCharacteristic(UUID.fromString(AppConstant.write_char_uuid))

            if (characteristic != null) {
                characteristic.value = byteValue
                val isWrite = mBluetoothGatt.writeCharacteristic(characteristic)
                BleDeviceActor.WaitForSync()
                Log.d("ble==>", "write characteristic: " + isWrite + " " + Arrays.toString(byteValue)+" hex: "+AppConstant.byteArrytoHex(byteValue))
                broadcastUpdate(AppConstant.ACTION_CHARACTERISTIC_WRITE, byteValue)
            } else {
                Log.d("ble==>", "characteristic null")
            }
        }


        private fun WaitForSync() {
            try {
                Thread.sleep(400)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }
}

