package com.sentinel.activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import java.util.*
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*

import com.sentinel.R.*
import com.ikovac.timepickerwithseconds.MyTimePickerDialog
import com.sentinel.R
import com.sentinel.ble.BleCharacteristic
import com.sentinel.ble.BleDeviceActor
import com.sentinel.util.AppConstant


class MainActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener{
    internal lateinit var iv_back: ImageView
    internal lateinit var iv_calender: ImageView
    internal lateinit var tv_title: TextView
    internal lateinit var tv_mc: TextView
    internal lateinit var tv_date: TextView
    internal lateinit var tv_send: TextView
    internal lateinit var tv_manual: TextView
    internal lateinit var tv_test: TextView
    internal lateinit var et_setOfDuration: EditText

    var day: Int = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    var second: Int = 0
    var dayOfWeek: Int = 0
    var isSendStartDate: Boolean = true
    var isSendDuration: Boolean = false
    lateinit var timePickerDialog: MyTimePickerDialog
    lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        initUI();
    }

    private fun initUI() {
        iv_back = findViewById(id.iv_back)
        tv_title = findViewById(id.tv_title)
        tv_mc = findViewById(id.tv_mc)
        tv_date = findViewById(id.tv_date)
        iv_calender = findViewById(id.iv_calender)
        et_setOfDuration = findViewById(id.et_setOfDuration)
        tv_send = findViewById(id.tv_send)
        tv_manual = findViewById(id.tv_manual)
        tv_test = findViewById(id.tv_test)

        iv_back.setOnClickListener(this)
        iv_calender.setOnClickListener(this)
        tv_send.setOnClickListener(this)
        tv_manual.setOnClickListener(this)
        tv_test.setOnClickListener(this)

        iv_back.visibility = View.VISIBLE
        tv_mc.visibility = View.VISIBLE
        tv_title.visibility = View.VISIBLE
        if (intent.extras != null) {
            tv_title.text = intent.getStringExtra("device_name")
            tv_mc.text = intent.getStringExtra("mac_address")
        }
        et_setOfDuration.setSelection(et_setOfDuration.text.toString().trim().length)

        val calendar: Calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        hour = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)
        second = calendar.get(Calendar.SECOND)
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        setDateText();
    }

    override fun onResume() {
        super.onResume()
        destroyAction()
        registerReceiver(mGattUpdateReceiver, AppConstant.makeIntentFilter())
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            id.iv_back -> {
                BleDeviceActor.disconnectDevice()
                destroyAction()
                val intentBack = Intent(this@MainActivity, ScanActivity::class.java)
                startActivity(intentBack)
                finish()
            }

            id.iv_calender -> {
                openDatePickerDialog()
            }

            id.tv_send -> {
                val duration:String = et_setOfDuration.text.toString().trim()
                if(duration.equals("")){
                    Toast.makeText(this,getString(R.string.enter_set_of_duration),Toast.LENGTH_SHORT).show()
                }else if(Integer.parseInt(duration)>255){
                    Toast.makeText(this,getString(R.string.set_of_duration_validation),Toast.LENGTH_SHORT).show()
                }else{
                    openSendConfimDialog()
                }
            }

            id.tv_manual -> {
                destroyAction()
                val intentManual = Intent(this@MainActivity, ManualActivity::class.java)
                startActivity(intentManual)
            }

            id.tv_test -> {
                destroyAction()
               val rtc:String = String.format("%02d", day) + "-" + String.format("%02d", (month + 1)) +
                        "-" + String.format("%02d", year) + " "+ String.format("%02d", dayOfWeek) + " " + String.format("%02d", hour) + ":" +
                        String.format("%02d", minute)+ ":" + String.format("%02d", second)
                val intentTest = Intent(this@MainActivity, TestActivity::class.java)
                intentTest.putExtra("RTC", rtc)
                startActivity(intentTest)
            }
        }
    }

    private fun openDatePickerDialog() {
        datePickerDialog =
            DatePickerDialog(this@MainActivity, this@MainActivity, year, month, day)
        datePickerDialog.show()
        datePickerDialog.setCancelable(false)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        day = dayOfMonth
        this.year = year
        this.month = month
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(year,month,day)
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (datePickerDialog != null && datePickerDialog.isShowing) {
            datePickerDialog.dismiss()
        }
        setDateText();
        openTimePicker()
    }

    private fun openTimePicker(){
        timePickerDialog = MyTimePickerDialog(this, object : MyTimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(
                view: com.ikovac.timepickerwithseconds.TimePicker?,
                hourOfDay: Int,
                minutePiced: Int,
                secondsPiced: Int
            ) {
                hour = hourOfDay
                minute = minutePiced
                second = secondsPiced
                setDateText();
                if (datePickerDialog != null && datePickerDialog.isShowing) {
                    datePickerDialog.dismiss()
                }
                if (timePickerDialog != null && timePickerDialog.isShowing) {
                    timePickerDialog.dismiss()
                }
            }
        },hour, minute,second, true)
        timePickerDialog.show()
        timePickerDialog.setCanceledOnTouchOutside(false)
    }

    private fun setDateText() {
        tv_date.text = String.format("%02d", day) + "-" + String.format("%02d", (month + 1)) +
                "-" + String.format("%02d", year) + " " + String.format("%02d", hour) + ":" +
                String.format("%02d", minute)+ ":" + String.format("%02d", second)
    }

    private fun openSendConfimDialog(){
        val dialog = Dialog(this, com.sentinel.R.style.dilogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(layout.dialog_send_confirm)
        var tv_date_dialog: TextView = dialog.findViewById(id.tv_date)
        var tv_setOfDuration: TextView = dialog.findViewById(id.tv_setOfDuration)
        var tv_dialog_send: TextView = dialog.findViewById(id.tv_dialog_send)
        var iv_close_dialog: ImageView = dialog.findViewById(id.iv_close_dialog)
        tv_date_dialog.text = tv_date.text.toString().trim()
        tv_setOfDuration.text = et_setOfDuration.text.toString().trim()
        iv_close_dialog.setOnClickListener() { v -> dialog.dismiss() }
        tv_dialog_send.setOnClickListener() { v ->
            writeStartDateToDevice()
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun writeStartDateToDevice(){
        isSendStartDate = true
        val last2DigitYear = year % 100
        val address:String = ""+String.format("%02X", 0xFF and last2DigitYear)+""+String.format("%02X", 0xFF and (month+1))+
                ""+String.format("%02X", 0xFF and day)+""+String.format("%02X", 0xFF and hour)+
                ""+String.format("%02X", 0xFF and minute)+""+String.format("%02X", 0xFF and second)

        BleCharacteristic.writeDataToDevice(this, AppConstant.START_DATE_COMMAND, address, "")
    }
    private fun writeSetDurationToDevice(){
        isSendStartDate = false
        isSendDuration = true
        val address:String = ""+String.format("%04X", 0xFF and Integer.parseInt(et_setOfDuration.text.toString().trim()))
        val addressByte:ByteArray = AppConstant.hexStringToByteArray(address).reversedArray()
        val addressUInt16:String = AppConstant.byteArrytoHex(addressByte)
        BleCharacteristic.writeDataToDevice(this, AppConstant.SET_DURATION_COMMAND, addressUInt16, "")
    }

    private fun writeRTCToDevice(){
        isSendDuration = false
        val last2DigitYear = year % 100
        val address:String = ""+String.format("%02X", 0xFF and last2DigitYear)+""+String.format("%02X", 0xFF and (month+1))+
                ""+String.format("%02X", 0xFF and day)+""+String.format("%02X", 0xFF and dayOfWeek)+""+String.format("%02X", 0xFF and hour)+
                ""+String.format("%02X", 0xFF and minute)+""+String.format("%02X", 0xFF and second)

        BleCharacteristic.writeDataToDevice(this, AppConstant.SET_RTC_DATE_COMMAND, address, "")
    }

    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                AppConstant.ACTION_DEVICE_DISCONNECTED -> {
                }

                AppConstant.ACTION_CHARACTERISTIC_CHANGED -> {
                    var data:ByteArray = intent.getByteArrayExtra("data")
                    if (data.size==2){
                        if (isSendStartDate && data[0].toInt()==0){
                            writeSetDurationToDevice()
                        }else  if (isSendDuration && data[0].toInt()==0){
                            writeRTCToDevice()
                        }else{
                            AppConstant.dismissProgrssDialog()
                            AppConstant.ShowResponseDialog(this@MainActivity,data)
                        }
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
