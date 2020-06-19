package com.sentinel.util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sentinel.R

object CheckSelfPermission {
    var permissiondiaog: AlertDialog? = null
    fun checkLocationPermission(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                AppConstant.REQUEST_LOCATION_PERMISSION
            )
            return false
        }
        return true
    }

    fun checkPhoneStatePermission(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                AppConstant.REQUEST_PHONE_STATE_PERMISSION
            )
            return false
        }
        return true
    }

    fun isLocationOn(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gps_enabled) {
            AlertDialog.Builder(context)
                .setMessage(R.string.enable_location_setting)
                .setPositiveButton(
                    R.string.ok,
                    DialogInterface.OnClickListener { paramDialogInterface, paramInt ->
                        (context as Activity).startActivityForResult(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                            AppConstant.REQUEST_ENABLE_LOCATION
                        )
                    })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false)
                .show()

            return false
        }
        return true
    }

    fun isBluetoothOn(context: Context): Boolean {
        val bAdapter = BluetoothAdapter.getDefaultAdapter() ?: return false
        if (!bAdapter.isEnabled) {
            (context as Activity).startActivityForResult(
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                AppConstant.REQUEST_ENABLE_BLUETOOTH
            )
            return false
        } else {
            return true
        }
    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    fun ShowPermissionAlert(context: Context, msg: String) {
        try {
            if (permissiondiaog != null) {
                if (permissiondiaog!!.isShowing) {
                    permissiondiaog!!.dismiss()
                }
            }
        } catch (e: Exception) {
        }

        permissiondiaog = AlertDialog.Builder(context)
            .setTitle(context.resources.getString(R.string.app_name))
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton("ok") { dialog, whichButton ->
                permissiondiaog!!.dismiss()
                openPermissionsSettings(context.packageName, context)
            }.show()
    }

    fun openPermissionsSettings(packageName: String, context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:$packageName")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                ActivityCompat.startActivityForResult(
                    context as Activity,
                    intent,
                    AppConstant.MY_MARSHMELLO_PERMISSION,
                    null
                )
            }
        } catch (e: Exception) {
        }

    }


}
