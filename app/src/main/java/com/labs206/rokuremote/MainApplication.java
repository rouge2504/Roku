package com.labs206.rokuremote;

import android.content.Context;
import android.content.Intent;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.my.tracker.MyTracker;
import com.my.tracker.MyTrackerParams;
import com.labs206.rokuremote.Utils.AdsManager;
import com.labs206.rokuremote.Utils.AppPreferences;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainApplication extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyTracker.initTracker("84146295512717276658", this);
        setUserInfo();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                List<String> testDeviceIds = Arrays.asList("F9A9B37BBA285A3CC3606F4AC0E67944","15A1F79C217F7B737BC7169EB5734AC8","BDA849508D5D6B696B016E85012958C5","D84C75A649CFC41441674EA8248A5470","461F40D98D74F224F7388F7639DA5CE1");
                RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
                MobileAds.setRequestConfiguration(configuration);
                Intent intent = new Intent("ADS_READY");
                sendBroadcast(intent);
                AdsManager.getInstance().checkNonEmptyContext(getApplicationContext());
                AdsManager.getInstance().adsIsReady = true;
                AdsManager.getInstance().initIntersitial();
            }
        });
    }

    public void setUserInfo()
    {
        String userId = AppPreferences.getInstance(this).getString("mtUserId");
        if(userId == null || userId.length() == 0) {
            try {
                userId = generateUserID();
            } catch (Exception e) {
                e.printStackTrace();
            }
            AppPreferences.getInstance(this).saveData("mtUserId",userId);
        }
        MyTrackerParams trackerParams = MyTracker.getTrackerParams();
        trackerParams.setCustomUserId(userId);
    }

    public String generateUserID() throws Exception{
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}
