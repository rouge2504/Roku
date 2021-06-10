package com.sensustech.rokuremote.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.my.tracker.MyTracker;

public class AdsManager implements BillingProcessor.IBillingHandler {

    private Context context;
    private static volatile AdsManager instance;
    private BillingProcessor bp;
    private InterstitialAd mInterstitialAd;
    public boolean adsIsReady = false;
    public boolean needReloadAds = false;

    public static AdsManager getInstance() {
        AdsManager localInstance = instance;
        if (localInstance == null) {
            synchronized (AdsManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new AdsManager();
                }
            }
        }
        return localInstance;
    }

    public void init(Context context) {
        this.context = context;
        bp = new BillingProcessor(context, null, this);
        bp.initialize();
    }

    public void checkNonEmptyContext(Context context) {
        if (this.context == null)
            this.context = context;
    }

    public void updateContext(Context context) {
        this.context = context;
    }

    public void initIntersitial() {
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("ca-app-pub-1543284146754967/3874922716");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    public void showAds() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public boolean checkPurchases() {
        return bp.loadOwnedPurchasesFromGoogle();
    }

    public boolean isPremium(Context ctx) {
        if (bp == null) {
            init(ctx);
        }
        if (AppPreferences.getInstance(ctx).getBoolean("isLifetimePremium") || (bp != null && bp.isPurchased("com.sensustech.rokuremote.lifetime")))
            return true;
        return false;
    }

    public BillingProcessor getBP() {
        return bp;
    }

    public void makePurchase(Activity activity, String purchaseId) {
        trackPremiumClicked();
        if (bp != null) {
            bp.purchase(activity, purchaseId);
        }
    }

    public void checkPremium() {
        try {
            if (context != null) {
                context.sendBroadcast(new Intent("CHECK_PREMIUM"));
            }
        } catch (Exception e) {
        }
    }

    public void closePremium() {
        try {
            if (context != null) {
                context.sendBroadcast(new Intent("CLOSE_PREMIUM"));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        trackPremiumCompleted();
        if (productId.equals("com.sensustech.rokuremote.lifetime")) {
            AppPreferences.getInstance(context).saveData("isLifetimePremium", true);
        }
        checkPremium();
        closePremium();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        checkPremium();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
    }

    @Override
    public void onBillingInitialized() {
        if (bp != null) {
            try {
                boolean isSubsUpdateSupported = bp.isSubscriptionUpdateSupported();
                if (isSubsUpdateSupported) {
                    bp.loadOwnedPurchasesFromGoogle();
                }
            } catch (Exception e) {
            }
        }
    }

    public void trackPremiumClicked() {
        MyTracker.trackEvent("premiumSelected");
        MyTracker.flush();
    }

    public void trackPremiumCompleted() {
        MyTracker.trackEvent("premiumCompleted");
        MyTracker.flush();
    }
}
