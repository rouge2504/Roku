package com.sensustech.rokuremote.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sensustech.rokuremote.Adapters.ChannelsAdapter;
import com.sensustech.rokuremote.Adapters.SettingsAdapter;
import com.sensustech.rokuremote.DevicesActivity;
import com.sensustech.rokuremote.Models.ChannelModel;
import com.sensustech.rokuremote.Models.DeviceModel;
import com.sensustech.rokuremote.PremActivity;
import com.sensustech.rokuremote.R;
import com.sensustech.rokuremote.Utils.AdsManager;
import com.sensustech.rokuremote.Utils.AppPreferences;
import com.sensustech.rokuremote.Utils.ItemClickSupport;
import com.sensustech.rokuremote.Utils.RecyclerItemClickListener;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    private SettingsAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private String titles[] = {"Connect", "Connect to other Roku", "Premium", "Premium", "Restore Purchases", "Support", "Contact Us", "Share App", "Rate Us" };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        recyclerView = root.findViewById(R.id.recycler_settings);
        adapter = new SettingsAdapter(getActivity(),titles);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if(titles[position].equals("Connect to other Roku")) {
                    startActivity(new Intent(SettingsFragment.this.getActivity(), DevicesActivity.class));
                }
                else if(titles[position].equals("Premium")) {
                    if(AdsManager.getInstance().isPremium(getActivity())) {
                        Toast.makeText(SettingsFragment.this.getContext(), "You are already premium!", Toast.LENGTH_LONG).show();

                    }else {
                        startActivity(new Intent(SettingsFragment.this.getActivity(), PremActivity.class));
                    }
                }
                else if(titles[position].equals("Restore Purchases")) {
                    restorePurchases();
                }
                else if(titles[position].equals("Contact Us")) {
                    openURL("mailto:support@sensustech.com");
                }
                else if(titles[position].equals("Share App")) {
                    shareAction();
                }
                else if(titles[position].equals("Rate Us")) {
                    openURL("https://play.google.com/store/apps/details?id=com.sensustech.rokuremote");
                }
            }
        });
        return root;
    }

    public void restorePurchases() {
        if (AdsManager.getInstance().checkPurchases()) {
            if (AdsManager.getInstance().isPremium(SettingsFragment.this.getContext())) {
                Toast.makeText(SettingsFragment.this.getContext(), "Your purchases was restored", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SettingsFragment.this.getContext(), "Your current Google Play Store account has no purchases", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(SettingsFragment.this.getContext(), "Your current Google Play Store account has no purchases", Toast.LENGTH_LONG).show();
        }
    }

    private void openURL(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }catch (ActivityNotFoundException e) {
            Toast.makeText(this.getContext(), "You don't have an app installed to open this URL.", Toast.LENGTH_LONG).show();
        }
    }

    private void shareAction() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Roku Remote allows you to control your Roku from Android device. https://play.google.com/store/apps/details?id=com.sensustech.rokuremote");
        Intent intent = Intent.createChooser(shareIntent, "Share");
        startActivity(intent);
    }
}