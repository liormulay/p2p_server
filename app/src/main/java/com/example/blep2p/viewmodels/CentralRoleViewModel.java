package com.example.blep2p.viewmodels;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import androidx.lifecycle.ViewModel;

import com.example.blep2p.model.DeviceModel;
import com.google.common.base.Strings;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class CentralRoleViewModel extends ViewModel {

    private final BehaviorSubject<ScanResult> resultsSubject = BehaviorSubject.create();
    private final BehaviorSubject<String> errorsSubject = BehaviorSubject.create();

    public void addScanResults(List<ScanResult> results) {
        for (ScanResult result : results) {
            resultsSubject.onNext(result);
        }
    }

    public void addScanResult(ScanResult result) {
        resultsSubject.onNext(result);
    }

    public void notifyError(String message) {
        errorsSubject.onNext(message);
    }

    public Observable<DeviceModel> getDevices() {
        return resultsSubject
                .filter(scanResult -> isFieldsValid(scanResult.getDevice()))
                .map(results -> new DeviceModel(results.getDevice().getName(), results.getDevice().getAddress(), results.getRssi()));
    }

    private boolean isFieldsValid(BluetoothDevice device) {
        return !Strings.isNullOrEmpty(device.getName())
                && !Strings.isNullOrEmpty(device.getAddress());
    }

    public Observable<String> getErrorMessages() {
        return errorsSubject.hide();
    }
}
