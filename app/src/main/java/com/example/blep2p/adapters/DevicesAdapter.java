package com.example.blep2p.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blep2p.R;
import com.example.blep2p.model.DeviceModel;
import com.example.blep2p.views.viewholders.DeviceViewHolder;
import com.google.common.base.Strings;

import java.util.ArrayList;

public class DevicesAdapter extends RecyclerView.Adapter<DeviceViewHolder> {


    public interface DevicesAdapterListener {
        void onDeviceItemClick(DeviceModel deviceModel);
    }


    private final ArrayList<DeviceModel> mArrayList;
    private final DevicesAdapterListener mListener;


    public DevicesAdapter(DevicesAdapterListener listener) {
        mArrayList = new ArrayList<>();
        mListener = listener;
    }


    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_device_list, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {

        DeviceModel deviceModel = mArrayList.get(position);
        holder.bindData(deviceModel);

        holder.itemView.setOnClickListener(view -> {
            if (!Strings.isNullOrEmpty(deviceModel.getDeviceAddress())) {
                if (mListener != null) {
                    mListener.onDeviceItemClick(deviceModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public void add(DeviceModel deviceModel) {
        add(deviceModel, true);
    }

    /**
     * Add a ScanResult item to the adapter if a result from that device isn't already present.
     * Otherwise updates the existing position with the new ScanResult.
     */
    public void add(DeviceModel deviceModel, boolean notify) {

        int existingPosition = getPosition(deviceModel.getDeviceAddress());

        if (existingPosition >= 0) {
            // Device is already in list, update its record.
            mArrayList.set(existingPosition, deviceModel);
        } else {
            // Add new Device's ScanResult to list.
            mArrayList.add(deviceModel);
        }

        if (notify) {
            notifyDataSetChanged();
        }

    }



    /**
     * Search the adapter for an existing device address and return it, otherwise return -1.
     */
    private int getPosition(String address) {
        int position = -1;
        for (int i = 0; i < mArrayList.size(); i++) {
            if (mArrayList.get(i).getDeviceAddress().equals(address)) {
                position = i;
                break;
            }
        }
        return position;
    }


}