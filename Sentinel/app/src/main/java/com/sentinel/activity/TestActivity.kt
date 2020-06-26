package com.sentinel.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.sentinel.R
import com.sentinel.ble.BleCharacteristic
import com.sentinel.util.AppConstant
import java.util.*

class TestActivity : AppCompatActivity(), View.OnClickListener {
    internal lateinit var iv_back: ImageView
    internal lateinit var tv_title: TextView
    internal lateinit var tv_rtc: TextView
    internal lateinit var tv_read: TextView
    internal lateinit var tv_write: TextView
    internal lateinit var tv_erase: TextView
    internal lateinit var tv_reset: TextView
    internal lateinit var tv_read_response: TextView
    internal lateinit var et_read_address: EditText
    internal lateinit var et_write_address: EditText
    internal lateinit var et_write_data: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        initUI()
        registerReceiver(mGattUpdateReceiver, AppConstant.makeIntentFilter())
    }

    private fun initUI() {
        iv_back = findViewById(R.id.iv_back)
        tv_title = findViewById(R.id.tv_title)
        tv_rtc = findViewById(R.id.tv_rtc)
        tv_read = findViewById(R.id.tv_read)
        tv_write = findViewById(R.id.tv_write)
        tv_erase = findViewById(R.id.tv_erase)
        tv_reset = findViewById(R.id.tv_reset)
        et_read_address = findViewById(R.id.et_read_address)
        tv_read_response = findViewById(R.id.tv_read_response)
        et_write_address = findViewById(R.id.et_write_address)
        et_write_data = findViewById(R.id.et_write_data)

        iv_back.setOnClickListener(this)
        tv_read.setOnClickListener(this)
        tv_write.setOnClickListener(this)
        tv_erase.setOnClickListener(this)
        tv_reset.setOnClickListener(this)

        iv_back.visibility = View.VISIBLE
        tv_title.text = resources.getString(R.string.test)
        if (intent.extras != null) {
            tv_rtc.text = intent.getStringExtra("RTC")
        }
    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.iv_back -> {
                destroyAction()
                finish()
            }

            R.id.tv_read -> {
                val address: String = et_read_address.text.toString().trim()
                if(address.equals("")){
                    Toast.makeText(this, resources.getString(R.string.enter_address_read), Toast.LENGTH_SHORT).show()
                }else{
                    if(address.length%2 == 0) {
                        BleCharacteristic.writeDataToDevice(this, AppConstant.READ_COMMAND, address, "")
                    }else{
                        Toast.makeText(this, resources.getString(R.string.enter_valid_data), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            R.id.tv_write -> {
                val address: String = et_write_address.text.toString().trim()
                val data: String = et_write_data.text.toString().trim()
                if(address.equals("")){
                    Toast.makeText(this, resources.getString(R.string.enter_address_write), Toast.LENGTH_SHORT).show()
                }else if(data.equals("")){
                    Toast.makeText(this, resources.getString(R.string.enter_data_write), Toast.LENGTH_SHORT).show()
                }else{
                    if(address.length%2 == 0 && data.length%2 == 0) {
                        BleCharacteristic.writeDataToDevice(this, AppConstant.WRITE_COMMAND, address, data)
                    }else{
                        Toast.makeText(this, resources.getString(R.string.enter_valid_data), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            R.id.tv_erase -> {
                val address: String = et_read_address.text.toString().trim()
                if(address.equals("")){
                    Toast.makeText(this, resources.getString(R.string.enter_address_erase), Toast.LENGTH_SHORT).show()
                }else{
                    if(address.length%2 == 0) {
                        BleCharacteristic.writeDataToDevice(this, AppConstant.ERASE_COMMAND, address, "")
                    }else{
                        Toast.makeText(this, resources.getString(R.string.enter_valid_data), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            R.id.tv_reset -> {
                BleCharacteristic.writeDeviceReset(this@TestActivity)
            }
        }
    }

    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                AppConstant.ACTION_DEVICE_DISCONNECTED -> {
                }

                AppConstant.ACTION_CHARACTERISTIC_CHANGED -> {
                    AppConstant.dismissProgrssDialog()
                    var data:ByteArray = intent.getByteArrayExtra("data")
                    if (data.size==2){
                        AppConstant.ShowResponseDialog(this@TestActivity,data)
                    }else if(data.size>6 && data[data.size-1].toInt()==10){
                        AppConstant.ShowResponseDialog(this@TestActivity,data)
                        data = Arrays.copyOfRange(data, 1, data.size - 5)
                        tv_read_response.text = resources.getString(R.string.read_response)+" "+AppConstant.byteArrytoHex(data)
                    }
                }
            }
        }
    }

   private fun destroyAction(){
        try {
            unregisterReceiver(mGattUpdateReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        destroyAction()
        super.onDestroy()
    }
}
