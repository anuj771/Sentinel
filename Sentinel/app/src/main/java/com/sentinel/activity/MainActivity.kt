package com.sentinel.activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import java.util.*
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*

import com.sentinel.R.*
import com.ikovac.timepickerwithseconds.MyTimePickerDialog
import com.sentinel.ble.BleDeviceActor



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
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        second = calendar.get(Calendar.SECOND)
        setDateText();
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            id.iv_back -> {
                BleDeviceActor.disconnectDevice()
                val intentBack = Intent(this@MainActivity, ScanActivity::class.java)
                startActivity(intentBack)
                finish()
            }

            id.iv_calender -> {
                openDatePickerDialog()
            }

            id.tv_send -> {
                openSendConfimDialog()
            }

            id.tv_manual -> {
                val intentManual = Intent(this@MainActivity, ManualActivity::class.java)
                startActivity(intentManual)
            }

            id.tv_test -> {
                val intentTest = Intent(this@MainActivity, TestActivity::class.java)
                intentTest.putExtra("RTC", tv_date.text.toString().trim())
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
            Toast.makeText(this@MainActivity, resources.getString(com.sentinel.R.string.success), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }
}
