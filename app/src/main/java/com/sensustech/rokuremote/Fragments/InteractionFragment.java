package com.sensustech.rokuremote.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sensustech.rokuremote.KeyboardActivity;
import com.sensustech.rokuremote.PremActivity;
import com.sensustech.rokuremote.R;
import com.sensustech.rokuremote.Utils.AdsManager;
import com.sensustech.rokuremote.Utils.AppPreferences;
import com.sensustech.rokuremote.Utils.OnSwipeTouchListener;
import com.sensustech.rokuremote.Utils.RokuControl;

public class InteractionFragment extends Fragment {

    private static final String TAG = "InteractionFragment";

    public InteractionFragment() {
    }

    public static InteractionFragment newInstance() {
        return new InteractionFragment();
    }

    private TextView tv_title;
    private TextView tv_swipe_check;
    private View view_swipe;
    private ImageButton btn_back;
    private ImageButton btn_keyboard;
    private ImageButton btn_help;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_interaction, container, false);

        tv_title = root.findViewById(R.id.tv_title);
        tv_swipe_check = root.findViewById(R.id.tv_swipe_check);
        view_swipe = root.findViewById(R.id.view_swipe);
        btn_back = root.findViewById(R.id.btn_back);
        btn_keyboard = root.findViewById(R.id.btn_keyboard);
        btn_help = root.findViewById(R.id.btn_help);

        view_swipe.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeTop() {
                tv_swipe_check.setText("Swipe Up");
                buttonClick("Up");
            }
            public void onSwipeRight() {
                tv_swipe_check.setText("Swipe Right");
                buttonClick("Right");
            }
            public void onSwipeLeft() {
                tv_swipe_check.setText("Swipe Left");
                buttonClick("Left");
            }
            public void onSwipeBottom() {
                tv_swipe_check.setText("Swipe Down");
                buttonClick("Down");
            }
            public void onSingleTap(){
                tv_swipe_check.setText("Tap");
                buttonClick("Select");
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick("Back");
            }
        });
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClick("Home");
            }
        });
        btn_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AdsManager.getInstance().isPremium(getActivity())) {
                    startActivity(new Intent(getActivity(), KeyboardActivity.class));
                }else {
                    startActivity(new Intent(getActivity(), PremActivity.class));
                }
            }
        });

        return root;
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