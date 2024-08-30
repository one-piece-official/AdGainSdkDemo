package com.gt.android.demo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

public class PxUtils {

    public static int dpToPx(Context context, int dp) {
        Resources r = context.getApplicationContext().getResources();
        float px = TypedValue.applyDimension(1, (float) dp, r.getDisplayMetrics());
        return (int) px;
    }

    public static int pxToDp(Context context, int px) {
        float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) ((float) px / scale + 0.5F);
    }

    public static int getDeviceWidthInPixel(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getDeviceHeightInPixel(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int getRealDeviceHeightInPixel(Context context) {
        DisplayMetrics dm = getRealMetrics(context);
        return dm.heightPixels;
    }

    public static DisplayMetrics getRealMetrics(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            @SuppressWarnings("rawtypes") Class c;
            try {
                c = Class.forName("android.view.Display");
                @SuppressWarnings("unchecked") Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, dm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dm;
    }
}
