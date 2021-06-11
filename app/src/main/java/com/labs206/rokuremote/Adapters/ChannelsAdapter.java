package com.labs206.rokuremote.Adapters;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.labs206.rokuremote.Models.ChannelModel;
import com.labs206.rokuremote.R;
import com.labs206.rokuremote.Utils.AppPreferences;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ChannelViewHolder> {

    private Activity activity;
    private ArrayList<ChannelModel> channels;

    public ChannelsAdapter(Activity activity, ArrayList<ChannelModel> channels) {
        this.activity = activity;
        this.channels = channels;
    }

    @NonNull
    @Override
    public ChannelsAdapter.ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.channels_item, parent, false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelsAdapter.ChannelViewHolder holder, int position) {
        String deviceIp = AppPreferences.getInstance(activity).getString("deviceIp");
        if(deviceIp != null && deviceIp.length() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Picasso.get().load("http://" + deviceIp + ":8060/query/icon/" + channels.get(position).appId).into(holder.image_preview);
            }else {
                Glide.with(activity).load("http://" + deviceIp + ":8060/query/icon/" + channels.get(position).appId).centerCrop().into(holder.image_preview);
            }
        }
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    public static class ChannelViewHolder extends RecyclerView.ViewHolder {

        private ImageView image_preview;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            image_preview = itemView.findViewById(R.id.image_preview);
        }
    }
}
