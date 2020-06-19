package com.sentinel.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sentinel.R
import com.sentinel.ble.BleDeviceActor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
