package com.example.blep2p.model;

public class DeviceModel {
    private String deviceName;
    private String deviceAddress;
    private int rssi;

    public DeviceModel() {
    }

    public DeviceModel(String deviceName, String deviceAddress, int rssi) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.rssi = rssi;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public int getRssi() {
        return rssi;
    }
}
