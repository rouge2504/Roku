package com.labs206.rokuremote.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.labs206.rokuremote.PremActivity;
import com.labs206.rokuremote.R;
import com.labs206.rokuremote.Utils.AdsManager;
import com.labs206.rokuremote.Utils.AppPreferences;
import com.labs206.rokuremote.Utils.RokuControl;

public class RemoteFragment extends Fragment {

    private static final String TAG = "RemoteFragment";

    public RemoteFragment() {
    }

    public static RemoteFragment newInstance() {
        return new RemoteFragment();
    }

    private ImageButton btn_vip;
    private ImageButton btn_on_off;
    private ImageButton btn_back;
    private ImageButton btn_home;
    private ImageButton btn_ok;
    private ImageButton btn_left;
    private ImageButton btn_up;
    private ImageButton btn_right;
    private ImageButton btn_down;
    private ImageButton btn_refresh;
    private ImageButton btn_record;
    private ImageButton btn_star;
    private ImageButton btn_backward;
    private ImageButton btn_play;
    private ImageButton btn_forward;
    private TextView deviceTextView;
    private ImageButton btn_volup;
    private ImageButton btn_voldown;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_remote_2, container, false);

        btn_vip = root.findViewById(R.id.btn_vip);
        btn_vip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PremActivity.class));
            }
        });

        deviceTextView = root.findViewById(R.id.tv_device_name);
        btn_on_off = root.findViewById(R.id.btn_on_off);
        btn_back = root.findViewById(R.id.btn_back);
        btn_home = root.findViewById(R.id.btn_home);
        btn_ok = root.findViewById(R.id.btn_ok);
        btn_left = root.findViewById(R.id.btn_left);
        btn_up = root.findViewById(R.id.btn_up);
        btn_right = root.findViewById(R.id.btn_right);
        btn_down = root.findViewById(R.id.btn_down);
        btn_refresh = root.findViewById(R.id.btn_refresh);
        btn_record = root.findViewById(R.id.btn_record);
        btn_star = root.findViewById(R.id.btn_star);
        btn_backward = root.findViewById(R.id.btn_backward);
        btn_play = root.findViewById(R.id.btn_play);
        btn_forward = root.findViewById(R.id.btn_forward);

        btn_voldown = root.findViewById(R.id.btn_voldown);
        btn_volup = root.findViewById(R.id.btn_volup);


        btn_down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        buttonPress("Down");
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonRelease("Down");
                        break;
                }
                return false;
            }
        });
        btn_right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        buttonPress("Right");
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonRelease("Right");
                        break;
                }
                return false;
            }
        });
        btn_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        buttonPress("Left");
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonRelease("Left");
                        break;
                }
                return false;
            }
        });
        btn_up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        buttonPress("Up");
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonRelease("Up");
                        break;
                }
                return false;
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick("Select");
            }
        });
        btn_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick("PowerOff");
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick("Back");
            }
        });
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick("Home");
            }
        });
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick("InstantReplay");
            }
        });
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick("VolumeMute");
            }
        });
        btn_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick("Info");
            }
        });
        btn_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick("Rev");
            }
        });
        btn_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick("Fwd");
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick("Play");
            }
        });
        btn_volup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        buttonPress("VolumeUp");
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonRelease("VolumeUp");
                        break;
                }
                return false;
            }
        });
        btn_voldown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        buttonPress("VolumeDown");
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonRelease("VolumeDown");
                        break;
                }
                return false;
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkDevice();
        checkPremium();
    }

    public void checkPremium() {
        if(AdsManager.getInstance().isPremium(getActivity()))
            btn_vip.setVisibility(View.GONE);
        else
            btn_vip.setVisibility(View.VISIBLE);
    }

    public void checkDevice() {
        String deviceName = AppPreferences.getInstance(getContext()).getString("deviceName");
        if(deviceName != null && deviceName.length() > 0){
            deviceTextView.setText(deviceName);
        }else {
            deviceTextView.setText("No Device");
        }
    }

    public void buttonPress(String buttonCode) {
        String deviceIp = AppPreferences.getInstance(getContext()).getString("deviceIp");
        if(deviceIp != null && deviceIp.length() > 0){
            RokuControl.getInstance().sendCommandPressHTTP(deviceIp,buttonCode);
        }else {
            Toast.makeText(this.getContext(), "No Roku Device Connected", Toast.LENGTH_LONG).show();
        }
    }

    public void buttonRelease(String buttonCode) {
        String deviceIp = AppPreferences.getInstance(getContext()).getString("deviceIp");
        if(deviceIp != null && deviceIp.length() > 0){
            RokuControl.getInstance().sendCommandReleaseHTTP(deviceIp,buttonCode);
        }else {
            Toast.makeText(this.getContext(), "No Roku Device Connected", Toast.LENGTH_LONG).show();
        }
        checkForAds();
    }

    public void buttonClick(String buttonCode) {
        String deviceIp = AppPreferences.getInstance(getContext()).getString("deviceIp");
        if(deviceIp != null && deviceIp.length() > 0){
            RokuControl.getInstance().sendCommandHTTP(deviceIp,buttonCode);
        }else {
            Toast.makeText(this.getContext(), "No Roku Device Connected", Toast.LENGTH_LONG).show();
        }
        checkForAds();
    }

    public void checkForAds() {
        if(!AdsManager.getInstance().isPremium(getActivity())) {
            int clicksCount = AppPreferences.getInstance(getContext()).getInt("buttonClicksCount", 0);
            int clicksInterval = AppPreferences.getInstance(getContext()).getInt("clicksAdsInterval", 5);
            clicksCount++;
            AppPreferences.getInstance(getContext()).saveData("buttonClicksCount", clicksCount);
            if (clicksCount % clicksInterval == 0) {
                AdsManager.getInstance().showAds();
            }
        }
    }
}