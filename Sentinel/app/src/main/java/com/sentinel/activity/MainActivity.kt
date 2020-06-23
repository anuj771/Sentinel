package com.sentinel.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.R
import com.sentinel.adapter.ScanListAdapter
import com.sentinel.ble.BleDeviceActor
import kotlinx.android.synthetic.main.activity_scan.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
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
    lateinit var timePickerDialog: TimePickerDialog
    lateinit var datePickerDialog: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI();
    }

    fun initUI() {
        iv_back = findViewById(R.id.iv_back)
        tv_title = findViewById(R.id.tv_title)
        tv_mc = findViewById(R.id.tv_mc)
        tv_date = findViewById(R.id.tv_date)
        iv_calender = findViewById(R.id.iv_calender)
        et_setOfDuration = findViewById(R.id.et_setOfDuration)
        tv_send = findViewById(R.id.tv_send)
        tv_manual = findViewById(R.id.tv_manual)
        tv_test = findViewById(R.id.tv_test)

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
        setDateText();
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.iv_back -> {
                finish()
            }

            R.id.iv_calender -> {
                openDatePickerDialog()
            }

            R.id.tv_send -> {

            }

            R.id.tv_manual -> {

            }

            R.id.tv_test -> {
                val intentTest = Intent(this@MainActivity, TestActivity::class.java)
                intentTest.putExtra("RTC", tv_date.text.toString().trim())
                startActivity(intentTest)
            }
        }
    }

    fun openDatePickerDialog() {
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
        timePickerDialog = TimePickerDialog(
            this@MainActivity, this@MainActivity, hour, minute,
            DateFormat.is24HourFormat(this)
        )
        timePickerDialog.show()
        timePickerDialog.setCancelable(false)
    }

    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
        this.hour = hourOfDay
        this.minute = minute
        setDateText();
        if (timePickerDialog != null && timePickerDialog.isShowing) {
            timePickerDialog.dismiss()
        }
    }

    fun setDateText() {
        String.format("%02d", day)
        tv_date.text = String.format("%02d", day) + "-" + String.format("%02d", (month + 1)) +
                "-" + String.format("%02d", year) + " " + String.format("%02d", hour) + ":" + String.format(
            "%02d",
            minute
        )
    }
}
