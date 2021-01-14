package com.example.blep2p.views.activities;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.example.blep2p.adapters.DevicesAdapter;
import com.example.blep2p.R;
import com.example.blep2p.SampleScanCallback;
import com.example.blep2p.model.DeviceModel;
import com.example.blep2p.viewmodels.CentralRoleViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * This activity represents the Central/Client role.
 * Bluetooth communication flow:
 * 1. advertise [peripheral]
 * 2. scan [central]
 * 3. connect [central]
 * 4. notify [peripheral]
 * 5. receive [central]
 */
public class CentralRoleActivity extends BluetoothActivity implements View.OnClickListener, DevicesAdapter.DevicesAdapterListener {


    /**
     * Stops scanning after 30 seconds.
     */
    private static final long SCAN_PERIOD = 30000;

    private RecyclerView mDevicesRecycler;
    private DevicesAdapter mDevicesAdapter;
    private AppCompatButton mScanButton;

    private ScanCallback mScanCallback;

    private Handler mHandler;

    private CentralRoleViewModel viewModel;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new CentralRoleViewModel();

        mScanButton = findViewById(R.id.button_scan);
        mScanButton.setOnClickListener(this);

        mDevicesRecycler = findViewById(R.id.devices_recycler_view);
        mDevicesRecycler.setHasFixedSize(true);
        mDevicesRecycler.setLayoutManager(new LinearLayoutManager(this));

        mDevicesAdapter = new DevicesAdapter(this);
        mDevicesRecycler.setAdapter(mDevicesAdapter);

        mHandler = new Handler(Looper.getMainLooper());

        viewModel = new CentralRoleViewModel();

        subscribe();
    }

    private void subscribe() {
        compositeDisposable.add(viewModel.getDevices()
        .subscribe(deviceModel -> mDevicesAdapter.add(deviceModel)));

        compositeDisposable.add(viewModel.getErrorMessages()
        .subscribe(this::showMsgText));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_central_role;
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_scan) {
            startBLEScan();
        }
    }


    @Override
    protected int getTitleString() {
        return R.string.central_screen;
    }


    /*
    start Bluetooth Low Energy scan
     */
    private void startBLEScan() {

        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();

        /*
        better to request each time as BluetoothAdapter state might change (connection lost, etc...)
         */
        if (bluetoothAdapter != null) {

            BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

            if (bluetoothLeScanner != null) {

                if (mScanCallback == null) {
                    Log.d(MainActivity.TAG, "Starting Scanning");

                    // Will stop the scanning after a set time.
                    mHandler.postDelayed(this::stopScanning, SCAN_PERIOD);

                    // Kick off a new scan.
                    mScanCallback = new SampleScanCallback(viewModel);
                    bluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), mScanCallback);

                    String toastText =
                            getString(R.string.scan_start_toast) + " "
                                    + TimeUnit.SECONDS.convert(SCAN_PERIOD, TimeUnit.MILLISECONDS) + " "
                                    + getString(R.string.seconds);

                    showMsgText(toastText);

                } else {
                    showMsgText(R.string.already_scanning);
                }

                return;
            }
        }

        showMsgText(R.string.error_unknown);
    }

    /**
     * Return a List of {@link ScanFilter} objects to filter by Service UUID.
     */
    private List<ScanFilter> buildScanFilters() {

        List<ScanFilter> scanFilters = new ArrayList<>();

        ScanFilter.Builder builder = new ScanFilter.Builder();
        scanFilters.add(builder.build());

        return scanFilters;
    }

    /**
     * Return a {@link ScanSettings} object set to use low power (to preserve battery life).
     */
    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        return builder.build();
    }


    /**
     * Stop scanning for BLE Advertisements.
     */
    public void stopScanning() {

        Log.d(MainActivity.TAG, "Stopping Scanning");

        /*
        better to request each time as BluetoothAdapter state might change (connection lost, etc...)
         */
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();

        if (bluetoothAdapter != null) {

            BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

            if (bluetoothLeScanner != null) {

                // Stop the scan, wipe the callback.
                bluetoothLeScanner.stopScan(mScanCallback);
                mScanCallback = null;

                // Even if no new results, update 'last seen' times.
                mDevicesAdapter.notifyDataSetChanged();

                return;
            }
        }

        showMsgText(R.string.error_unknown);
    }


    @Override
    public void onDeviceItemClick(DeviceModel deviceModel) {

        stopScanning();

        Intent intent = new Intent(this, DeviceConnectActivity.class);
        intent.putExtra(DeviceConnectActivity.EXTRAS_DEVICE_NAME, deviceModel.getDeviceName());
        intent.putExtra(DeviceConnectActivity.EXTRAS_DEVICE_ADDRESS, deviceModel.getDeviceAddress());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}