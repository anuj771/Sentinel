package com.sentinel.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.sentinel.CircularWheelView
import com.sentinel.R
import com.sentinel.ble.BleCharacteristic
import com.sentinel.util.AppConstant


class ManualActivity : AppCompatActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    internal lateinit var iv_back: ImageView
    internal lateinit var tv_title: TextView
    internal lateinit var tv_position: TextView
    internal lateinit var iv_position_selector: ImageView
    internal lateinit var iv_wheel: ImageView
    internal lateinit var toggle_fan: ToggleButton
    internal lateinit var circularWheelPicker: CircularWheelView
    var isFromPicker = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.sentinel.R.layout.activity_manual)
        initui()
    }

    override fun onResume() {
        super.onResume()
        destroyAction()
        registerReceiver(mGattUpdateReceiver, AppConstant.makeIntentFilter())
    }

    private fun initui() {
        iv_back = findViewById(com.sentinel.R.id.iv_back)
        tv_title = findViewById(com.sentinel.R.id.tv_title)
        tv_position = findViewById(com.sentinel.R.id.tv_position)
        iv_position_selector = findViewById(com.sentinel.R.id.iv_position_selector)
        iv_wheel = findViewById(com.sentinel.R.id.iv_wheel)
        toggle_fan = findViewById(com.sentinel.R.id.toggle_fan)
        circularWheelPicker = findViewById(com.sentinel.R.id.circularWheelPicker)

        iv_back.setOnClickListener(this)
        iv_position_selector.setOnClickListener(this)
        toggle_fan.setOnCheckedChangeListener(this)

        iv_back.visibility = View.VISIBLE
        tv_title.text = resources.getString(com.sentinel.R.string.manual)

        setWheelPicker();
    }

    private fun setWheelPicker() {
        val list2 = ArrayList<String>()
        for (i in 1 until 17 step 1) {
            list2.add(i.toString())
        }

        circularWheelPicker.setDataSet(list2)
        circularWheelPicker.setWheelItemSelectionListener(object : CircularWheelView.WheelItemSelectionListener {
            override fun onItemSelected(index: Int) {
                if (!isFromPicker) {
                    tv_position.text = String.format("%02d", index + 1)
                    writeGoToPosition(index)
                }
                isFromPicker = false;
//                Log.d("wheel==>", "Selected position is : $index")
//                Log.d("wheel==>", "Get Current Item : ${circularWheelPicker.getCurrentItem()}")
//                Log.d("wheel==>", "Get Current Position : ${circularWheelPicker.getCurrentPosition()}")
            }
        })
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            com.sentinel.R.id.iv_back -> {
                destroyAction()
                finish()
            }

            com.sentinel.R.id.iv_position_selector -> {
                opneNumberPickPopup(p0)
            }
        }
    }

    private fun opneNumberPickPopup(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menu.add("1")
        popupMenu.menu.add("2")
        popupMenu.menu.add("3")
        popupMenu.menu.add("4")
        popupMenu.menu.add("5")
        popupMenu.menu.add("6")
        popupMenu.menu.add("7")
        popupMenu.menu.add("8")
        popupMenu.menu.add("9")
        popupMenu.menu.add("10")
        popupMenu.menu.add("11")
        popupMenu.menu.add("12")
        popupMenu.menu.add("13")
        popupMenu.menu.add("14")
        popupMenu.menu.add("15")
        popupMenu.menu.add("16")
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                val int1: Int = (item.toString()).toInt()
                tv_position.text = String.format("%02d", int1)
//                setWheelIcon(int1)
                isFromPicker = true;
                circularWheelPicker.setCurrentPosition(int1 - 1)
                writeGoToPosition(int1)
                return true
            }
        })
        popupMenu.show()
    }

    private fun setWheelIcon(position: Int) {
        when (position) {
            1 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel1))
            2 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel2))
            3 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel3))
            4 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel4))
            5 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel5))
            6 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel6))
            7 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel7))
            8 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel8))
            9 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel9))
            10 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel10))
            11 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel11))
            12 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel12))
            13 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel13))
            14 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel14))
            15 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel15))
            16 -> iv_wheel.setImageDrawable(resources.getDrawable(R.drawable.wheel16))
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        writeFan(p1)
    }

    private fun writeGoToPosition(position: Int) {
        val address: String = "" + String.format("%02X", 0xFF and position)
        BleCharacteristic.writeDataToDevice(this, AppConstant.GO_TO_POSITION_COMMAND, address, "")
    }

    private fun writeFan(isFan: Boolean) {
        if (isFan) {
            BleCharacteristic.writeDataToDevice(this, AppConstant.FAN_COMMAND, "01", "")
        } else {
            BleCharacteristic.writeDataToDevice(this, AppConstant.FAN_COMMAND, "00", "")
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
                    var data: ByteArray = intent.getByteArrayExtra("data")
                    if (data.size == 2) {
                        AppConstant.ShowResponseDialog(this@ManualActivity, data)
                    }
                }
            }
        }
    }

    private fun destroyAction() {
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
