package com.sensustech.rokuremote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.sensustech.rokuremote.Adapters.DevicesAdapter;
import com.sensustech.rokuremote.Models.DeviceModel;
import com.sensustech.rokuremote.Utils.AppPreferences;
import com.sensustech.rokuremote.Utils.ItemClickSupport;

import java.util.ArrayList;

public class DevicesActivity extends AppCompatActivity implements DiscoveryManagerListener {

    private DevicesAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ImageButton btn_close;
    private ProgressBar progress;
    private ArrayList<DeviceModel> devices = new ArrayList<DeviceModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        recyclerView = findViewById(R.id.recycler_devices);
        btn_close = findViewById(R.id.btn_close);
        progress = findViewById(R.id.progress);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        adapter = new DevicesAdapter(this, devices);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if(position != RecyclerView.NO_POSITION && position >= 0 && position < devices.size()) {
                    DeviceModel device = devices.get(position);
                    AppPreferences.getInstance(DevicesActivity.this).saveData("deviceIp", device.deviceIp);
                    AppPreferences.getInstance(DevicesActivity.this).saveData("deviceName", device.name);
                    finish();
                }
            }
        });
        DiscoveryManager.init(getApplicationContext());
        startSearch();
    }

    public void startSearch() {
        DiscoveryManager.getInstance().registerDefaultDeviceTypes();
        DiscoveryManager.getInstance().setPairingLevel(DiscoveryManager.PairingLevel.ON);
        DiscoveryManager.getInstance().addListener(this);
        DiscoveryManager.getInstance().start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DiscoveryManager.getInstance().stop();
    }

    @Override
    public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
        if((device.getManufacturer() != null && device.getManufacturer().toLowerCase().contains("roku")) || isRoku(device)) {
            DeviceModel dm = new DeviceModel();
            dm.name = device.getFriendlyName();
            dm.series = device.getModelName();
            dm.deviceIp = device.getIpAddress();
            if (!devices.contains(dm)) {
                devices.add(dm);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {

    }

    @Override
    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
        for (DeviceModel dm : devices) {
            if (dm.name.equals(device.getFriendlyName())) {
                devices.remove(dm);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public final boolean isRoku(ConnectableDevice connectableDevice) {
        try {
            if (connectableDevice != null) {
                String connectedServiceNames = connectableDevice.getConnectedServiceNames();
                if (connectedServiceNames != null) {
                    String lowerCase = connectedServiceNames.toLowerCase();
                    return lowerCase.contains("roku");
                }
            }
        }catch (Exception e){}
        return false;
    }

}