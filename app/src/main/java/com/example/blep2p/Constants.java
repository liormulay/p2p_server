package com.example.blep2p;

import java.util.UUID;

public class Constants {


    public static final int SERVER_MSG_FIRST_STATE = 1;
    public static final int SERVER_MSG_SECOND_STATE = 2;

    /*
    better to use different Bluetooth Service,
    instead of Heart Rate Service:
    https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.service.heart_rate.xml.
    maybe Object Transfer Service is more suitable:
    https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.service.object_transfer.xml
     */
    public static final UUID HEART_RATE_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
    public static final UUID BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID = UUID.fromString("00002A38-0000-1000-8000-00805f9b34fb");


}