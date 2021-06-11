package com.labs206.rokuremote.Utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class ScreenChecker {

    public static double screenSize(Activity activity){
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float yInches= metrics.heightPixels/metrics.ydpi;
        float xInches= metrics.widthPixels/metrics.xdpi;
        return Math.sqrt(xInches*xInches + yInches*yInches);
    }

}
