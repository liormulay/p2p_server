package com.example.blep2p;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.example.blep2p.viewmodels.CentralRoleViewModel;
import com.example.blep2p.views.activities.MainActivity;

import java.util.List;

/**
 * Custom ScanCallback object - adds to adapter on success, displays error on failure.
 */
public class SampleScanCallback extends ScanCallback {

    private final CentralRoleViewModel viewModel;

    public SampleScanCallback(CentralRoleViewModel viewModel) {
        this.viewModel = viewModel;
    }


    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        super.onBatchScanResults(results);
        viewModel.addScanResults(results);
        logResults(results);
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        viewModel.addScanResult(result);
        logResults(result);
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
        viewModel.notifyError("Scan failed with error: " + errorCode);
    }


    private void logResults(List<ScanResult> results) {
        if (results != null) {
            for (ScanResult result : results) {
                logResults(result);
            }
        }
    }

    private void logResults(ScanResult result) {
        if (result != null) {
            BluetoothDevice device = result.getDevice();
            if (device != null) {
                Log.v(MainActivity.TAG, device.getName() + " " + device.getAddress());
                return;
            }
        }
        Log.e(MainActivity.TAG, "error SampleScanCallback");
    }
}
