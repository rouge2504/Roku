package com.labs206.rokuremote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anjlab.android.iab.v3.SkuDetails;
import com.labs206.rokuremote.Utils.AdsManager;
import com.labs206.rokuremote.R;

import java.util.ArrayList;

public class PremActivity extends AppCompatActivity {

    private ImageButton btn_close;
    private Button btn_pay;
    private Button btn_yearly;
    private Button btn_four_weeks;
    private TextView tv_privacy;
    private CardView card_pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prem);

        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_pay = findViewById(R.id.btn_pay);
        btn_yearly = findViewById(R.id.btn_yearly);
        btn_four_weeks = findViewById(R.id.btn_four_weeks);
        tv_privacy = findViewById(R.id.tv_privacy);
        card_pay = findViewById(R.id.card_pay);
        Animation anim = new ScaleAnimation(
                1f, 1.075f, // Start and end values for the X axis scaling
                1f, 1.075f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(900);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        card_pay.setAnimation(anim);
        AdsManager.getInstance().updateContext(this);
        readPrices();
        registerReceiver(premiumBroadcast, new IntentFilter("CLOSE_PREMIUM"));
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsManager.getInstance().makePurchase(PremActivity.this, "unlock_all");
            }
        });
        btn_yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsManager.getInstance().makePurchase(PremActivity.this, "yearly_subscription");
            }
        });

        btn_four_weeks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsManager.getInstance().makePurchase(PremActivity.this, "four_weeks");
            }
        });

    }

    public void readPrices() {
        try {
            if (AdsManager.getInstance().getBP() != null && AdsManager.getInstance().getBP().isInitialized()) {
                new readPricesAsync().execute();
            }
        } catch (Exception e) {
        }
    }

    private class readPricesAsync extends AsyncTask<Void, Void, ArrayList<SkuDetails>> {
        @Override
        protected ArrayList<SkuDetails> doInBackground(Void... voids) {
            //SkuDetails skuLifetime = AdsManager.getInstance().getBP().getPurchaseListingDetails("unlock_all");
            ArrayList<SkuDetails> purchaseListingDetails = new ArrayList<SkuDetails>();
            purchaseListingDetails.add(AdsManager.getInstance().getBP().getPurchaseListingDetails("unlock_all"));
            purchaseListingDetails.add(AdsManager.getInstance().getBP().getPurchaseListingDetails("yearly_subscription"));
            purchaseListingDetails.add(AdsManager.getInstance().getBP().getPurchaseListingDetails("four_weeks"));
            return purchaseListingDetails;
        }

        @Override
        protected void onPostExecute(ArrayList<SkuDetails> list) {
            if (list.get(0) != null) {
                btn_pay.setText("FULL VERSION " + list.get(0).priceText);
                btn_yearly.setText("Yearly VERSION " + list.get(1).priceText);
                btn_four_weeks.setText("4 weeks VERSION " + list.get(2).priceText);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(premiumBroadcast);
    }

    BroadcastReceiver premiumBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeOffer();
    }

    public void closeOffer() {
        System.out.println(AdsManager.getInstance().isPremium(this));
        if (!AdsManager.getInstance().isPremium(this)) {
            AdsManager.getInstance().showAds();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (AdsManager.getInstance().getBP() != null && !AdsManager.getInstance().getBP().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}