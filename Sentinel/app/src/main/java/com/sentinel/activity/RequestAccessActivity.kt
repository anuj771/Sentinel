package com.sentinel.activity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.sentinel.R
import com.sentinel.ble.BleCharacteristic

import java.util.*
import com.sentinel.ble.BleDeviceActor
import com.sentinel.util.AppConstant
import com.sentinel.util.CheckSelfPermission
import com.sentinel.util.CheckSelfPermission.checkPhoneStatePermission

class RequestAccessActivity : AppCompatActivity(), View.OnClickListener {
    private var tv_text: TextView? = null

    private var btScanner: BluetoothLeScanner? = null
    private var btManager: BluetoothManager? = null
    private var btAdapter: BluetoothAdapter? = null
    private var timer: Timer? = null
    private var isConnecting = false

    private val ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val scannedDevice = result.device
            if (scannedDevice != null) {
                if (result.scanRecord!!.deviceName != null) {
                    Log.d("ble==> ", "ScanCallback: " + result.scanRecord!!.deviceName!!)
                    stopScan()
                    if (!isConnecting) {
                        isConnecting = true
                        val bleDeviceActor = BleDeviceActor(this@RequestAccessActivity)
                        bleDeviceActor.connectToDevice(scannedDevice, this@RequestAccessActivity)
                    }
                }
            }
        }
    }

    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val previosText = tv_text!!.text.toString().trim();
            when (action) {
                AppConstant.ACTION_DEVICE_CONNECTED -> {
                    tv_text!!.setText(previosText + "\nConnected");
                }

                AppConstant.ACTION_DEVICE_DISCONNECTED -> {
                    tv_text!!.setText(previosText + "\ndisConnected");
                    isConnecting = false
                    startScan()
                }

                AppConstant.ACTION_CHARACTERISTIC_CHANGED -> {
                    /*   val data = intent.getByteArrayExtra("data")
                       if (data!![0].toInt() == 17) {
                           BleCharacteristic.writeDataToDevice(this@RequestAccessActivity, data)
                       } else if (data[0].toInt() == 18) {
                           if (data[1].toInt() == 0) {
                               setDeniedUi()
                           } else if (data[1].toInt() == 1) {
                               setSuccessUi()
                           }
                       }*/
                }

                AppConstant.ACTION_CHARACTERISTIC_WRITE -> {
                    val data = intent.getByteArrayExtra("data")
                    tv_text!!.setText(previosText + "\nwrite " + AppConstant.byteArrytoHex(data));

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_access)

        initUI()
        registerReceiver(mGattUpdateReceiver, AppConstant.makeIntentFilter())
    }

    override fun onResume() {
        super.onResume()
        checkSelfPermission()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_text -> {
                val command = byteArrayOf(0x00)
                val address = byteArrayOf(0x00, 0x50, 0x00)
                BleCharacteristic.writeDataToDevice(this, command, address)
            }
        }
    }


    private fun moveToBackground() {
        Log.d("ble==> ", "moveToBackground ")
        val i = Intent()
        i.action = Intent.ACTION_MAIN
        i.addCategory(Intent.CATEGORY_HOME)
        this.startActivity(i)
    }

    private fun initUI() {
        tv_text = findViewById(R.id.tv_text)
        tv_text!!.setOnClickListener(this)
    }

    private fun checkSelfPermission() {
        if (CheckSelfPermission.checkLocationPermission(this@RequestAccessActivity)) {
            if (CheckSelfPermission.isBluetoothOn(this@RequestAccessActivity)) {
                if (CheckSelfPermission.isLocationOn(this@RequestAccessActivity)) {
                    if (!isConnecting) {
                        startScan()
                    }
                }
            }
        }
    }

    private fun startScan() {
        //        stopTimerScan();
        stopScan()
        //        setTimer();
        btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager!!.adapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btScanner = btAdapter!!.bluetoothLeScanner
            val scanSettingsBuilder = ScanSettings.Builder()
            scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)

            val filters = ArrayList<ScanFilter>()
            val filter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(UUID.fromString(AppConstant.service_uuid)))
                .build()
            filters.add(filter)
            if (btScanner != null) {
                btScanner!!.startScan(filters, scanSettingsBuilder.build(), ScanCallback)
                Log.d("ble==> ", "startScan ")
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun stopScan() {
        Log.d("ble==> ", "stopScan ")
        if (btAdapter != null && btAdapter!!.isEnabled) {
            if (btScanner != null)
                btScanner!!.stopScan(ScanCallback)
        }
    }

    private fun setTimer() {
        stopTimerScan()
        timer = Timer()
        val hourlyTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    stopTimerScan()
                    moveToBackground()
                }
            }
        }
        timer!!.schedule(hourlyTask, 5000, 10)
    }

    private fun stopTimerScan() {
        //        Log.d("ble==> ", "stopTimerScan ");
        try {
            if (timer != null) {
                timer!!.cancel()
                timer = null
            }
        } catch (e: Exception) {
        }

    }

    override fun onDestroy() {
        stopScan()
        try {
            unregisterReceiver(mGattUpdateReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onDestroy()
    }
}
