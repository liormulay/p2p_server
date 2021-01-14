package com.example.blep2p.activities;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.blep2p.services.PeripheralAdvertiseService;
import com.example.blep2p.R;

import java.util.Arrays;
import java.util.HashSet;

import static com.example.blep2p.Constants.BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID;
import static com.example.blep2p.Constants.HEART_RATE_SERVICE_UUID;

/**
 * This activity represents the Peripheral/Server role.
 * Bluetooth communication flow:
 * 1. advertise [peripheral]
 * 2. scan [central]
 * 3. connect [central]
 * 4. notify [peripheral]
 * 5. receive [central]
 */
public class PeripheralRoleActivity extends BluetoothActivity implements View.OnClickListener {

    private BluetoothGattService mSampleService;
    private BluetoothGattCharacteristic mSampleCharacteristic;

    private BluetoothManager mBluetoothManager;
    private BluetoothGattServer mGattServer;
    private HashSet<BluetoothDevice> mBluetoothDevices;

    private AppCompatButton mNotifyButton;
    private SwitchCompat mEnableAdvertisementSwitch;
    private AppCompatEditText advertiseEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotifyButton = findViewById(R.id.button_notify);
        mEnableAdvertisementSwitch = findViewById(R.id.advertise_switch);
        advertiseEditText = findViewById(R.id.advertise_editText);

        if (enableNavigation) {
            mEnableAdvertisementSwitch.setEnabled(true);
        }

        mNotifyButton.setOnClickListener(this);
        mEnableAdvertisementSwitch.setOnClickListener(this);

        advertiseEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.toString().isEmpty()) {
                    PeripheralRoleActivity.this.setCharacteristic(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setGattServer();
        setBluetoothService();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_peripheral_role;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.advertise_switch:
                SwitchCompat switchToggle = (SwitchCompat) view;
                if (switchToggle.isChecked()) {
                    startAdvertising();
                } else {
                    stopAdvertising();
                }
                break;


            case R.id.button_notify:
                notifyCharacteristicChanged();
                break;

        }
    }


    @Override
    protected int getTitleString() {
        return R.string.peripheral_screen;
    }


    /**
     * Starts BLE Advertising by starting {@code PeripheralAdvertiseService}.
     */
    private void startAdvertising() {
        startService(getServiceIntent(this));
    }


    /**
     * Stops BLE Advertising by stopping {@code PeripheralAdvertiseService}.
     */
    private void stopAdvertising() {
        stopService(getServiceIntent(this));
        mEnableAdvertisementSwitch.setChecked(false);
    }

    private void setGattServer() {

        mBluetoothDevices = new HashSet<>();
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (mBluetoothManager != null) {
            mGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);
        } else {
            showMsgText(R.string.error_unknown);
        }
    }

    private void setBluetoothService() {

        // create the Service
        mSampleService = new BluetoothGattService(HEART_RATE_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        /*
        create the Characteristic.
        we need to grant to the Client permission to read (for when the user clicks the "Request Characteristic" button).
        no need for notify permission as this is an action the Server initiate.
         */
        mSampleCharacteristic = new BluetoothGattCharacteristic(BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID, BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);

        // add the Characteristic to the Service
        mSampleService.addCharacteristic(mSampleCharacteristic);

        // add the Service to the Server/Peripheral
        if (mGattServer != null) {
            mGattServer.addService(mSampleService);
        }
    }


    /*
    update the value of Characteristic.
    the client will receive the Characteristic value when:
        1. the Client user clicks the "Request Characteristic" button
        2. teh Server user clicks the "Notify Client" button

     */
    private void setCharacteristic(CharSequence value) {
        mSampleCharacteristic.setValue(getValue(value));
    }

    private byte[] getValue(CharSequence value) {
        return new byte[]{(byte) value.charAt(0)};
    }


    /*
    send to the client the value of the Characteristic,
    as the user requested to notify.
     */
    private void notifyCharacteristicChanged() {
        /*
        done when the user clicks the notify button in the app.
        indicate - true for indication (acknowledge) and false for notification (un-acknowledge).
         */
        boolean indicate = (mSampleCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) == BluetoothGattCharacteristic.PROPERTY_INDICATE;

        for (BluetoothDevice device : mBluetoothDevices) {
            if (mGattServer != null) {
                mGattServer.notifyCharacteristicChanged(device, mSampleCharacteristic, indicate);
            }
        }
    }

    /**
     * Returns Intent addressed to the {@code PeripheralAdvertiseService} class.
     */
    private Intent getServiceIntent(Context context) {
        return new Intent(context, PeripheralAdvertiseService.class);
    }


    private final BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onConnectionStateChange(BluetoothDevice device, final int status, int newState) {

            super.onConnectionStateChange(device, status, newState);

            String msg;

            if (status == BluetoothGatt.GATT_SUCCESS) {

                if (newState == BluetoothGatt.STATE_CONNECTED) {

                    mBluetoothDevices.add(device);

                    msg = "Connected to device: " + device.getAddress();
                    Log.v(TAG, msg);
                    showMsgText(msg);

                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {

                    mBluetoothDevices.remove(device);

                    msg = "Disconnected from device";
                    Log.v(TAG, msg);
                    showMsgText(msg);
                }

            } else {
                mBluetoothDevices.remove(device);

                msg = getString(R.string.status_error_when_connecting) + ": " + status;
                Log.e(TAG, msg);
                showMsgText(msg);

            }
        }


        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            Log.v(TAG, "Notification sent. Status: " + status);
        }


        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {

            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);

            if (mGattServer == null) {
                return;
            }

            Log.d(TAG, "Device tried to read characteristic: " + characteristic.getUuid());
            Log.d(TAG, "Value: " + Arrays.toString(characteristic.getValue()));

            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
        }


        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {

            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);

            Log.v(TAG, "Characteristic Write request: " + Arrays.toString(value));

            mSampleCharacteristic.setValue(value);

            if (responseNeeded) {
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);
            }

        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {

            super.onDescriptorReadRequest(device, requestId, offset, descriptor);

            if (mGattServer == null) {
                return;
            }

            Log.d(TAG, "Device tried to read descriptor: " + descriptor.getUuid());
            Log.d(TAG, "Value: " + Arrays.toString(descriptor.getValue()));

            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, descriptor.getValue());
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded,
                                             int offset,
                                             byte[] value) {

            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);

            Log.v(TAG, "Descriptor Write Request " + descriptor.getUuid() + " " + Arrays.toString(value));


        }
    };


}