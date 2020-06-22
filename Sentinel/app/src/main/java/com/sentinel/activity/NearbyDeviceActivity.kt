package com.sentinel.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.R
import com.sentinel.adapter.ScanListAdapter
import com.sentinel.ble.BleCharacteristic
import com.sentinel.ble.BleDeviceActor
import com.sentinel.model.ScanListModel
import com.sentinel.util.AppConstant
import com.sentinel.util.CheckSelfPermission
import kotlinx.android.synthetic.main.activity_request_access.*

import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

class NearbyDeviceActivity : AppCompatActivity() {
   /* internal lateinit var iv_back: ImageView
    internal lateinit var text_tilte: TextView
    internal lateinit var tv_scan: TextView
    internal lateinit var rv_scan_list: RecyclerView
    internal lateinit var deviceListAdapter: ScanListAdapter
    internal var scanModellists = ArrayList<ScanListModel>()

    private var btScanner: BluetoothLeScanner? = null
    private var btManager: BluetoothManager? = null
    private var btAdapter: BluetoothAdapter? = null
    private var timer50Sec: Timer? = null
    internal var devicename = ""
    internal var macAddress = ""
    internal var password = ""

    private val ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val scannedDevice = result.device
            if (scannedDevice != null) {
                if (!isContainScanList(scannedDevice.address)) {
                    if (result.scanRecord!!.deviceName != null) {
                        scanModellists.add(
                            ScanListModel(
                                result.scanRecord!!.deviceName,
                                scannedDevice.address,
                                false,
                                scannedDevice
                            )
                        )
                    } else {
                        scanModellists.add(ScanListModel("", scannedDevice.address, false,  scannedDevice))
                    }
                    deviceListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private var pDialog: ProgressDialog? = null


    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val previosText = tv_text!!.text.toString().trim();
            when (action) {
                AppConstant.ACTION_DEVICE_CONNECTED -> {
                    hideProgressDialog()
                    val intent1 = Intent(context, MainActivity::class.java)
                    intent1.putExtra("device_name", devicename)
                    intent1.putExtra("mac_address", macAddress)
                    context.startActivity(intent1)
                }

                AppConstant.ACTION_DEVICE_DISCONNECTED -> {
                    hideProgressDialog()
                }
            }
        }
    }

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        initUI()
    }

    fun initUI() {
        text_tilte = findViewById(R.id.tv_title)
        text_tilte.text = resources.getString(R.string.nearby_devices)
        tv_scan = findViewById(R.id.tv_scan)

        rv_scan_list = findViewById(R.id.rv_scan_list)
        rv_scan_list.layoutManager = LinearLayoutManager(this@NearbyDeviceActivity, RecyclerView.VERTICAL, false)
        deviceListAdapter = ScanListAdapter(this@NearbyDeviceActivity, scanModellists)
        rv_scan_list.adapter = deviceListAdapter
        tv_scan.setOnClickListener {
            stopScan()
            chechSelfPermission()
        }
        iv_back.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        chechSelfPermission()
        registerReceiver(mGattUpdateReceiver, AppConstant.makeIntentFilter())
        scanModellists.clear()
        deviceListAdapter.notifyDataSetChanged()

    }

    private fun chechSelfPermission() {
        if (CheckSelfPermission.checkLocationPermission(this@NearbyDeviceActivity)) {
            if (CheckSelfPermission.isBluetoothOn(this@NearbyDeviceActivity)) {
                if (CheckSelfPermission.isLocationOn(this@NearbyDeviceActivity)) {
                    try {
                        scanModellists.clear()
                        deviceListAdapter.notifyDataSetChanged()
                        startScan()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppConstants.REQUEST_LOCATION_PERMISSION) {
            if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //                chechSelfPermission();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // user rejected the permission
                val showRationale = shouldShowRequestPermissionRationale(permissions[0])
                if (!showRationale) {
                    CheckSelfPermission.ShowPermissionAlert(
                        this@NearbyDeviceActivity,
                        getString(R.string.allow_storage_permission)
                    )
                } else {
                    //                    chechSelfPermission();
                }
            }
            return
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.REQUEST_ENABLE_BLUETOOTH -> if (resultCode == 0) {
                Toast.makeText(
                    this@NearbyDeviceActivity,
                    getString(R.string.enable_bluetooth_toast),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (resultCode == Activity.RESULT_OK) {
                //                    chechSelfPermission();
            }

            AppConstants.REQUEST_ENABLE_LOCATION -> {
            }
        }//                chechSelfPermission();
    }


    private fun startScan() {
        setTimer30Sec()
        btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager!!.adapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btScanner = btAdapter!!.bluetoothLeScanner
            val scanSettingsBuilder = ScanSettings.Builder()
            scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)

            val filters = ArrayList<ScanFilter>()
            stopScan()
            if (btScanner != null) {
                btScanner!!.startScan(filters, scanSettingsBuilder.build(), ScanCallback)
                refreshAdapter()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun stopScan() {
        if (btAdapter!!.isEnabled) {
            if (btScanner != null)
                btScanner!!.stopScan(ScanCallback)
        }
    }

    private fun setTimer30Sec() {
        timer50Sec = Timer()
        val hourlyTask = object : TimerTask() {
            override fun run() {
                runOnUiThread { stopTimerScan() }
            }
        }
        timer50Sec!!.schedule(hourlyTask, 50000, 50000)
    }

    private fun stopTimerScan() {
        try {
            if (timer50Sec != null) {
                timer50Sec!!.cancel()
                timer50Sec = null
            }
        } catch (e: Exception) {
        }

    }

    private fun isContainScanList(mac: String): Boolean {
        for (model in scanModellists) {
            if (model.deviceMacAddress.equals(mac)) {
                return true
            }
        }
        return false
    }

    fun OpenConnectDialog(DeviceName: String, Macaddress: String, Pass: String) {
        devicename = DeviceName
        macAddress = Macaddress
        password = Pass
        pDialog = ProgressDialog(this)
        pDialog!!.setMessage(resources.getString(R.string.connecting) + "...")
        pDialog!!.setCancelable(false)
        pDialog!!.setCanceledOnTouchOutside(false)
        pDialog!!.show()
    }

    private fun hideProgressDialog() {
        try {
            if (pDialog!!.isShowing) {
                pDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun refreshAdapter() {
        try {
            if (timer != null) {
                timer!!.cancel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        timer = Timer()
        val hourlyTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    try {
                        deviceListAdapter.notifyDataSetChanged()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        timer!!.schedule(hourlyTask, 0L, 1500)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPause() {
        try {
            stopTimerScan()
            stopScan()
            hideProgressDialog()
            unregisterReceiver(mGattUpdateReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            timer!!.cancel()
        } catch (e: Exception) {
        }
        super.onPause()
    }*/
}