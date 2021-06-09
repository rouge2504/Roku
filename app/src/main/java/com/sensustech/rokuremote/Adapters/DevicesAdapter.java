package com.sensustech.rokuremote.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensustech.rokuremote.Models.ChannelModel;
import com.sensustech.rokuremote.Models.DeviceModel;
import com.sensustech.rokuremote.R;

import org.w3c.dom.Text;

import java.nio.channels.Channel;
import java.util.ArrayList;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder> {

    private Activity activity;
    private ArrayList<DeviceModel> devices;

    public DevicesAdapter(Activity activity, ArrayList<DeviceModel> devices) {
        this.activity = activity;
        this.devices = devices;
    }

    @NonNull
    @Override
    public DevicesAdapter.DevicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_item, parent, false);
        return new DevicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesAdapter.DevicesViewHolder holder, int position) {
        holder.tv_name.setText(devices.get(position).name);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class DevicesViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;

        public DevicesViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
