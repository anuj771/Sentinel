package com.sentinel.activity

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.R
import com.sentinel.adapter.ScanListAdapter
import com.sentinel.model.ScanListModel
import com.sentinel.util.AppConstant
import com.sentinel.util.CheckSelfPermission
import java.util.*

class ScanActivity : AppCompatActivity() {

    internal lateinit var text_tilte: TextView
    internal lateinit var tv_scan: TextView
    internal lateinit var rv_scan_list: RecyclerView
    internal lateinit var deviceListAdapter: ScanListAdapter
    internal var scanModellists = ArrayList<ScanListModel>()

    private var btScanner: BluetoothLeScanner? = null
    private var btManager: BluetoothManager? = null
    private var btAdapter: BluetoothAdapter? = null
    internal var devicename = ""
    internal var macAddress = ""
    private var pDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        initUI()
        registerReceiver(mGattUpdateReceiver, AppConstant.makeIntentFilter())
    }

    override fun onResume() {
        super.onResume()
        chechSelfPermission()
        scanModellists.clear()
        deviceListAdapter.notifyDataSetChanged()

    }

    override fun onPause() {
        stopScan();
        super.onPause()
    }

    override fun onDestroy() {
        try {
            stopScan()
            unregisterReceiver(mGattUpdateReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    fun initUI() {
        text_tilte = findViewById(R.id.tv_title)
        text_tilte.text = resources.getString(R.string.nearby_devices)
        tv_scan = findViewById(R.id.tv_scan)

        rv_scan_list = findViewById(R.id.rv_scan_list)
        rv_scan_list.layoutManager = LinearLayoutManager(this@ScanActivity, RecyclerView.VERTICAL, false)
        deviceListAdapter = ScanListAdapter(this@ScanActivity, scanModellists)
        rv_scan_list.adapter = deviceListAdapter
        tv_scan.setOnClickListener {
            chechSelfPermission()
        }
    }

    private fun chechSelfPermission() {
        if (CheckSelfPermission.checkLocationPermission(this@ScanActivity)) {
            if (CheckSelfPermission.isBluetoothOn(this@ScanActivity)) {
                if (CheckSelfPermission.isLocationOn(this@ScanActivity)) {
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

    private fun startScan() {
        btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager!!.adapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btScanner = btAdapter!!.bluetoothLeScanner
            val scanSettingsBuilder = ScanSettings.Builder()
            scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)

            val filters = ArrayList<ScanFilter>()
           /* val filter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(UUID.fromString(AppConstant.service_uuid)))
                .build()
            filters.add(filter)*/
            stopScan()
            if (btScanner != null) {
                btScanner!!.startScan(filters, scanSettingsBuilder.build(), ScanCallback)
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun stopScan() {
        if (btAdapter!=null && btAdapter!!.isEnabled) {
            if (btScanner != null)
                btScanner!!.stopScan(ScanCallback)
        }
    }

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
                        scanModellists.add(ScanListModel("N/A", scannedDevice.address, false,  scannedDevice))
                    }
                    deviceListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                AppConstant.ACTION_DEVICE_CONNECTED -> {
                    hideProgressDialog()
                    val intent1 = Intent(context, MainActivity::class.java)
                    intent1.putExtra("device_name", devicename)
                    intent1.putExtra("mac_address", macAddress)
                    context.startActivity(intent1)
                    finish()
                }

                AppConstant.ACTION_DEVICE_DISCONNECTED -> {
                    hideProgressDialog()
                }
            }
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

    fun OpenConnectDialog(DeviceName: String, Macaddress: String) {
        devicename = DeviceName
        macAddress = Macaddress
        pDialog = ProgressDialog(this)
        pDialog!!.setMessage(resources.getString(R.string.connecting) + "...")
        pDialog!!.setCancelable(false)
        pDialog!!.setCanceledOnTouchOutside(false)
        pDialog!!.show()
    }

    private fun hideProgressDialog() {
        try {
            if (pDialog!=null && pDialog!!.isShowing) {
                pDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

 /*    private var timer: Timer? = null
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

    }*/
}
