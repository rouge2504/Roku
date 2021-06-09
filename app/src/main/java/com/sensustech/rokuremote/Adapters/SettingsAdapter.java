package com.sensustech.rokuremote.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sensustech.rokuremote.PremActivity;
import com.sensustech.rokuremote.R;

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    public static final String TAG = "SettingsAdapter";

    private Activity activity;

    private static final int VIEW_TYPE_SECTION = 1;
    private static final int VIEW_TYPE_ITEM = 2;
    int selectedPosition = -1;
    private String titles[];

    public SettingsAdapter(Activity activity, String[] titles) {
        this.activity = activity;
        this.titles = titles;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 2 || position == 5) {
            return VIEW_TYPE_SECTION;
        } else {
            return VIEW_TYPE_ITEM;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SECTION) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_section_item, parent, false);
            return new SectionViewHolder(view);
        } else if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.settings_item, parent, false);
            return new ItemViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SECTION:
                ((SectionViewHolder) holder).title.setText(titles[position]);
                break;
            case VIEW_TYPE_ITEM:
                if (position == 1){
                    ((ItemViewHolder) holder).image.setBackgroundResource(R.drawable.settings_i_connect);
                } else if (position == 3){
                    ((ItemViewHolder) holder).image.setBackgroundResource(R.drawable.settings_i_premium);
                } else if (position == 4){
                    ((ItemViewHolder) holder).image.setBackgroundResource(R.drawable.settings_i_restor);
                } else if (position == 6){
                    ((ItemViewHolder) holder).image.setBackgroundResource(R.drawable.settings_i_contact);
                } else if (position == 7){
                    ((ItemViewHolder) holder).image.setBackgroundResource(R.drawable.settings_i_share);
                } else if (position == 8){
                    ((ItemViewHolder) holder).image.setBackgroundResource(R.drawable.settings_i_rate_us);
                }
                ((ItemViewHolder) holder).title.setText(titles[position]);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 9;
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        private TextView title;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_name);

        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "ItemViewHolder";
        private TextView title;
        private ImageView image;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_name);
            image = itemView.findViewById(R.id.image);
        }
    }
}
