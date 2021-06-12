package com.labs206.rokuremote.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.my.tracker.MyTracker;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        if (CheckDate(ctx)){
            return false;
        }
        if (AppPreferences.getInstance(ctx).getBoolean("isLifetimePremium") || (bp != null && bp.isPurchased("unlock_all")))
            return true;

        if (AppPreferences.getInstance(ctx).getBoolean("isYearly") || (bp != null && bp.isPurchased("yearly_subscription")))
            return true;

        if (AppPreferences.getInstance(ctx).getBoolean("isWeek") || (bp != null && bp.isPurchased( "four_weeks")))
            return true;

        return false;
    }

    public boolean CheckDate(Context ctx){
        if (TextUtils.isEmpty(AppPreferences.getInstance(ctx).getString("YearEnabled"))
                || TextUtils.isEmpty(AppPreferences.getInstance(ctx).getString("WeekEnabled"))){

            return false;
        }
        Date expiredDate = stringToDate(AppPreferences.getInstance(ctx).getString("YearEnabled"), "yyyy-mm-dd hh:mm:ss");
        if (new Date().after(expiredDate))
            return false;

        expiredDate = stringToDate(AppPreferences.getInstance(ctx).getString("WeekEnabled"), "yyyy-mm-dd hh:mm:ss");
        if (new Date().after(expiredDate))
            return false;

        return true;
    }

    private Date stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

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
        if (productId.equals("unlock_all")) {
            AppPreferences.getInstance(context).saveData("isLifetimePremium", true);
        }
        else if (productId.equals("yearly_subscription")) {
            AppPreferences.getInstance(context).saveData("isYearly", true);
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, 1);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            date = calendar.getTime();
            String strDate = dateFormat.format(date);
            AppPreferences.getInstance(context).saveData("YearEnabled", strDate);
            System.out.println(calendar.getTime());
        }

        else if (productId.equals("four_weeks")) {
            AppPreferences.getInstance(context).saveData("isWeek", true);
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.WEEK_OF_YEAR, 4);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            date = calendar.getTime();
            String strDate = dateFormat.format(date);
            AppPreferences.getInstance(context).saveData("WeekEnabled", strDate);
            System.out.println(calendar.getTime());
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
