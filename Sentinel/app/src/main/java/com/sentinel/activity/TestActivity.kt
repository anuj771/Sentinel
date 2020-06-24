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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_request_access.*
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.activity_test.view.*
import kotlinx.android.synthetic.main.layout_header.*
import java.util.*

class TestActivity : AppCompatActivity(), View.OnClickListener {
    internal lateinit var iv_back: ImageView
    internal lateinit var tv_title: TextView
    internal lateinit var tv_rtc: TextView
    internal lateinit var tv_read: TextView
    internal lateinit var tv_write: TextView
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
        et_read_address = findViewById(R.id.et_read_address)
        tv_read_response = findViewById(R.id.tv_read_response)
        et_write_address = findViewById(R.id.et_write_address)
        et_write_data = findViewById(R.id.et_write_data)

        iv_back.setOnClickListener(this)
        tv_read.setOnClickListener(this)
        tv_write.setOnClickListener(this)

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
                        tv_read.alpha = 0.5f
                        BleCharacteristic.writeDataToDevice(this, 0, address, "")
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
                        tv_write.alpha = 0.5f
                        BleCharacteristic.writeDataToDevice(this, 1, address, data)
                    }else{
                        Toast.makeText(this, resources.getString(R.string.enter_valid_data), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                AppConstant.ACTION_DEVICE_CONNECTED -> {
                }

                AppConstant.ACTION_DEVICE_DISCONNECTED -> {
                }

                AppConstant.ACTION_CHARACTERISTIC_CHANGED -> {
                    tv_read.alpha = 1f
                    var data:ByteArray = intent.getByteArrayExtra("data")
                    if ((intent.getByteArrayExtra("data")[0]).toInt()==0 && data.size>6){
                        if((intent.getByteArrayExtra("data")[intent.getByteArrayExtra("data").size-1]).toInt()==10) {
                            data = Arrays.copyOfRange(data, 1, data.size - 5)
                            tv_read_response.text = resources.getString(R.string.read_response)+" "+AppConstant.byteArrytoHex(data)
                        }
                    }else if(data.size==2 && data[0].toInt()==0){
                        tv_write.alpha = 1f
                        Toast.makeText(this@TestActivity, resources.getString(R.string.success), Toast.LENGTH_SHORT).show()
                    }
                }

                AppConstant.ACTION_CHARACTERISTIC_WRITE -> {

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
