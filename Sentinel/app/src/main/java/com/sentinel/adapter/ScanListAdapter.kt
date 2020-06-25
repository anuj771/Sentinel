package com.sentinel.adapter

import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.R
import com.sentinel.activity.ScanActivity
import com.sentinel.ble.BleDeviceActor
import com.sentinel.model.ScanListModel
import com.sentinel.util.CheckSelfPermission

class ScanListAdapter
    (private val context: Context, var mData: List<ScanListModel>) :
    RecyclerView.Adapter<ScanListAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    init {
        this.mInflater = LayoutInflater.from(context)
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.adapter_scan_list, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scanModellist = mData[position]
        holder.tv_deviceName.setText(scanModellist.Devicename)
        holder.tv_macAddress.setText(scanModellist.deviceMacAddress)
        holder.tv_connect.setOnClickListener {
            if (CheckSelfPermission.checkLocationPermissionRetional(context)) {
                if (CheckSelfPermission.isBluetoothOn(context)) {
                    if (CheckSelfPermission.isLocationOn(context)) {
                        if (context is ScanActivity) {
                            (context as ScanActivity).OpenConnectDialog(
                                scanModellist.Devicename,
                                scanModellist.deviceMacAddress
                            )
                        }
                        val bleDeviceActor = BleDeviceActor(context)
                        val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                        val btAdapter = btManager.adapter
                        BleDeviceActor.disconnectDevice()
                        bleDeviceActor.connectToDevice(btAdapter.getRemoteDevice(scanModellist.deviceMacAddress),context)
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var tv_deviceName: TextView
        internal var tv_macAddress: TextView
        internal var tv_connect: TextView

        init {
            tv_connect = itemView.findViewById(R.id.tv_connect)
            tv_deviceName = itemView.findViewById(R.id.tv_deviceName)
            tv_macAddress = itemView.findViewById(R.id.tv_macAddress)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }
    }

    fun getItem(id: Int): String {
        return mData[id].deviceMacAddress
    }

    fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    companion object {
        fun hideKeyboardFrom(context: Context, view: View) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}