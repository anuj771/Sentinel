package com.sentinel.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*


class ManualActivity : AppCompatActivity(), View.OnClickListener {
    internal lateinit var iv_back: ImageView
    internal lateinit var tv_title: TextView
    internal lateinit var tv_position: TextView
    internal lateinit var iv_position_selector: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.sentinel.R.layout.activity_manual)
        initui()
    }

    private fun initui() {
        iv_back = findViewById(com.sentinel.R.id.iv_back)
        tv_title = findViewById(com.sentinel.R.id.tv_title)
        tv_position = findViewById(com.sentinel.R.id.tv_position)
        iv_position_selector = findViewById(com.sentinel.R.id.iv_position_selector)

        iv_back.setOnClickListener(this)
        iv_position_selector.setOnClickListener(this)

        iv_back.visibility = View.VISIBLE
        tv_title.text = resources.getString(com.sentinel.R.string.manual)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            com.sentinel.R.id.iv_back -> {
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
                val int1: Int? = (item.toString()).toInt()
                tv_position.text = String.format("%02d", int1)
                return true
            }
        })
        popupMenu.show()
    }


}
