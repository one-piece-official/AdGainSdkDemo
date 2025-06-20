package com.adgain.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.adgain.sdk.Constants;
import com.adgain.demo.android.R;

public class UIUtil {

    public static int getScreenWidthInPx(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeightInPx(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    //获取屏幕真实高度
    public static int getRealHeightInPx(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            display.getMetrics(dm);
        }
        int realHeight = dm.heightPixels;
        return realHeight;
    }

    public static void showBottomUIMenu(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    //隐藏虚拟按键，并且全屏
    public static void hideBottomUIMenu(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                View v = activity.getWindow().getDecorView();
                v.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                //for new api versions.
                View decorView = activity.getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        //                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE;
                decorView.setSystemUiVisibility(uiOptions);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "hideBottomUIMenu error: ", e);
        }
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / (scale <= 0 ? 1 : scale) + 0.5f);
    }

    public static void removeViewFromParent(View view) {
        if (null == view) return;
        ViewParent vp = view.getParent();
        if (vp instanceof ViewGroup) {
            ((ViewGroup) vp).removeView(view);
        }
    }

    public static LinearLayout createAdButtonsLayout(
            Context context,
            String prefix,
            String adUnitID,
            ViewGroup parent,
            View.OnClickListener clickListener) {

        View view = LayoutInflater.from(context).inflate(R.layout.ad_buttons_layout, parent, true);
        Button btnLoadAd = view.findViewById(R.id.btn_load_ad);
        Button btnShowAd = view.findViewById(R.id.btn_show_ad);
        
        btnLoadAd.setText(prefix + " LOAD-" + adUnitID);
        btnLoadAd.setOnClickListener(clickListener);
        btnShowAd.setText(prefix + " SHOW-" + adUnitID);
        btnShowAd.setOnClickListener(clickListener);
        
        return (LinearLayout) view;
    }

    public static int getARandomColor() {
        int a = Double.valueOf(Math.random() * 255).intValue();
        int r = Double.valueOf(Math.random() * 255).intValue();
        int g = Double.valueOf(Math.random() * 255).intValue();
        int b = Double.valueOf(Math.random() * 255).intValue();
        return Color.argb(a, r, g, b);
    }
}
