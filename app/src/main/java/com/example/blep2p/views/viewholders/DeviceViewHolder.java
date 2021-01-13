package com.example.blep2p.views.viewholders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blep2p.R;
import com.example.blep2p.model.DeviceModel;

import java.text.MessageFormat;

public class DeviceViewHolder extends RecyclerView.ViewHolder {
    private final AppCompatTextView mDeviceNameView;
    private final AppCompatTextView mDeviceNameAddressView;
    private final AppCompatTextView rssiTextView;


    public DeviceViewHolder(@NonNull View itemView) {
        super(itemView);
        mDeviceNameView = itemView.findViewById(R.id.device_name);
        mDeviceNameAddressView = itemView.findViewById(R.id.device_address);
        rssiTextView = itemView.findViewById(R.id.device_rssi);
    }

    public void bindData(DeviceModel deviceModel) {
        mDeviceNameView.setText(deviceModel.getDeviceName());
        mDeviceNameAddressView.setText(deviceModel.getDeviceAddress());
        rssiTextView.setText(MessageFormat.format("RSSI: {0}", deviceModel.getRssi()));
    }
}
