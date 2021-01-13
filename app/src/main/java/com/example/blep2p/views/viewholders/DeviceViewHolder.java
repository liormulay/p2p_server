package com.example.blep2p.views.viewholders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blep2p.R;
import com.example.blep2p.model.DeviceModel;

public class DeviceViewHolder extends RecyclerView.ViewHolder {
    AppCompatTextView mDeviceNameView;
    AppCompatTextView mDeviceNameAddressView;

    public DeviceViewHolder(@NonNull View itemView) {
        super(itemView);
        mDeviceNameView = itemView.findViewById(R.id.device_name);
        mDeviceNameAddressView = itemView.findViewById(R.id.device_address);
    }

    public void bindData(DeviceModel deviceModel) {
        mDeviceNameView.setText(deviceModel.getDeviceName());
        mDeviceNameAddressView.setText(deviceModel.getDeviceAddress());
    }
}
