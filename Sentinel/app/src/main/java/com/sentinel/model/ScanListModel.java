package com.sentinel.model;

import android.bluetooth.BluetoothDevice;

public class ScanListModel {
    public String deviceMacAddress;
    public String Devicename;
    public Boolean isSectectDevice;
    public BluetoothDevice bluetoothDevice;

    public ScanListModel(String deviceName, String deviceMacAddress, Boolean isSectectDevice, BluetoothDevice bluetoothDevice) {
        this.Devicename = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.isSectectDevice = isSectectDevice;
        this.bluetoothDevice = bluetoothDevice;
    }
}