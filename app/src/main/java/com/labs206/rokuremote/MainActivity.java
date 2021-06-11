package com.labs206.rokuremote;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.labs206.rokuremote.Fragments.ChannelsFragment;
import com.labs206.rokuremote.Fragments.InteractionFragment;
import com.labs206.rokuremote.Fragments.RemoteFragment;
import com.labs206.rokuremote.Fragments.SettingsFragment;
import com.labs206.rokuremote.Utils.AdsManager;
import com.labs206.rokuremote.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import hotchemi.android.rate.AppRate;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    private FrameLayout frame;
    private BottomNavigationView bottomNav;
    private UnifiedNativeAd nativeAdBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frame = findViewById(R.id.host_fragment);
        bottomNav = findViewById(R.id.bottom_view);
        bottomNav.setOnNavigationItemSelectedListener(this);
        loadFragment(RemoteFragment.newInstance());
        registerReceiver(adsBroadcast, new IntentFilter("ADS_READY"));
        startActivity(new Intent(this, DevicesActivity.class));
        try {
            AdsManager.getInstance().init(this);
            if(AdsManager.getInstance().needReloadAds) {
                AdsManager.getInstance().needReloadAds = false;
                refreshAdBack();
            }
        } catch (Exception e) {}
        checkRate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(adsBroadcast);
    }

    BroadcastReceiver adsBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshAdBack();
        }
    };

    private void refreshAdBack() {
        if(!AdsManager.getInstance().isPremium(this)) {
            AdLoader.Builder builder = new AdLoader.Builder(this, "ca-app-pub-4975991316875268/5219498821");
            builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                @Override
                public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                    if (nativeAdBack != null) {
                        nativeAdBack.destroy();
                    }
                    nativeAdBack = unifiedNativeAd;
                }

            });
            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(false).build();
            NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
            builder.withNativeAdOptions(adOptions);
            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int errorCode) {}
            }).build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remote:
                loadFragment(RemoteFragment.newInstance());
                return true;
            case R.id.interaction:
                loadFragment(InteractionFragment.newInstance());
                return true;
            case R.id.channels:
                loadFragment(ChannelsFragment.newInstance());
                return true;
            case R.id.settings:
                loadFragment(SettingsFragment.newInstance());
                return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.host_fragment, fragment);
        ft.commit();
    }

    public void destroyAds() {
        AdsManager.getInstance().needReloadAds = true;
        try {
            if (nativeAdBack != null) {
                nativeAdBack.destroy();
            }
        }catch (Exception e){}
    }

    @Override
    public void onBackPressed() {
        showBackDialog();
    }

    public void showBackDialog() {
        try {
            AlertDialog.Builder backDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogTheme))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            destroyAds();
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .setCancelable(false);
            if (!AdsManager.getInstance().isPremium(this) && nativeAdBack != null) {
                RelativeLayout backDialogLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.back_dialog, null);
                FrameLayout frameLayout = backDialogLayout.findViewById(R.id.back_ads);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.ad_unified, null);
                populateUnifiedNativeAdView(nativeAdBack, adView);
                frameLayout.addView(adView);
                backDialog.setView(backDialogLayout);
            } else {
                backDialog.setMessage("Are you sure that you want to leave the app?");
            }
            backDialog.create();
            backDialog.show();
        }catch (Exception e) {
           e.printStackTrace();
            finish(); }
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);
    }

    private void checkRate(){
        try {
            AppRate.with(this)
                    .setInstallDays(1)
                    .setLaunchTimes(5)
                    .setRemindInterval(3)
                    .setShowLaterButton(true)
                    .setDebug(false)
                    .setMessage("If you enjoy our Roku Remote, would you mind taking a moment to rate it? It won't take more than a minute. Thanks for your support!")
                    .monitor();
            AppRate.showRateDialogIfMeetsConditions(this);
        }catch (Exception e){}
    }

}