package com.gt.android.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import com.gt.android.demo.utils.PxUtils;
import com.gt.sdk.api.AdError;
import com.gt.sdk.api.AdRequest;
import com.gt.sdk.api.SplashAd;
import com.gt.sdk.api.SplashAdListener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends Activity {
    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    public boolean canJumpImmediately = false;
    private SplashAd splashAd;
    private ViewGroup adContainer;
    private String codeId;
    private String userId = "123456789";

    // 是否适配全面屏，默认是适配全面屏，即使用顶部状态栏和底部导航栏
    private boolean isNotchAdaptation = true;

    private void getExtraInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("setting", 0);
        codeId = sharedPreferences.getString(Constants.CONF_PLACEMENT_ID, "");

        if (TextUtils.isEmpty(codeId)) {
            String[] stringArray = getResources().getStringArray(R.array.splash_id_value);
            codeId = stringArray[0];
        }
    }

    private void hideSystemUI() {
        int systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        } else {
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        Window window = this.getWindow();
        window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        // 五要素隐私详情页或五要素弹窗关闭回到开屏广告时，再次设置SystemUi
        window.getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> setSystemUi());

        // Android P 官方方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(params);
        }
    }

    private void showSystemUI() {
        int systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        Window window = this.getWindow();
        window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        // 五要素隐私详情页或五要素弹窗关闭回到开屏广告时，再次设置SystemUi
        window.getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> setSystemUi());
    }

    private void setSystemUi() {
        if (!isNotchAdaptation) {
            showSystemUI();
        } else {
            hideSystemUI();
        }
    }

    public int dipsToIntPixels(final float dips, final Context context) {
        return (int) (dips * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    private DisplayMetrics getRealMetrics(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            @SuppressWarnings("rawtypes")
            Class c;
            try {
                c = Class.forName("android.view.Display");
                @SuppressWarnings("unchecked")
                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, dm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 如需适配刘海屏水滴屏，必须在onCreate方法中设置全屏显示
        if (isNotchAdaptation) {
            hideSystemUI();
        }

        setContentView(R.layout.activity_splash);
        adContainer = findViewById(R.id.splash_container);

        getExtraInfo();

        Map<String, String> options = new HashMap<>();
        options.put("user_id", userId);

        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(codeId)
                .setWidth(PxUtils.getDeviceWidthInPixel(this))
                .setHeight(PxUtils.getRealDeviceHeightInPixel(this) - dipsToIntPixels(100, this))
                .setExtOption(options)
                .build();

        splashAd = new SplashAd(adRequest, new SplashAdListener() {
            @Override
            public void onSplashAdLoadSuccess(String codeId) {
                Log.d(Constants.TAG, "------onSplashAdLoadSuccess------" + splashAd.isReady() + ":" + codeId);
                if (splashAd != null && splashAd.isReady()) {
                    splashAd.showAd(adContainer);
                }
            }

            @Override
            public void onSplashAdLoadFail(String codeId, AdError error) {
                Log.d(Constants.TAG, "------onSplashAdLoadFail------" + error + ":" + codeId);
                jumpMainActivity();
            }

            @Override
            public void onSplashAdShow(String codeId) {
                Log.d(Constants.TAG, "------onSplashAdShow------" + codeId);
            }

            @Override
            public void onSplashAdShowError(String codeId, AdError error) {
                Log.d(Constants.TAG, "------onSplashAdShowError------" + error + ":" + codeId);
                jumpMainActivity();
            }

            @Override
            public void onSplashAdClick(String codeId) {
                Log.d(Constants.TAG, "------onSplashAdClick------" + codeId);
            }

            @Override
            public void onSplashAdClose(String codeId) {
                Log.d(Constants.TAG, "------onSplashAdClose------" + codeId);
                jumpWhenCanClick();
            }
        });
        splashAd.loadAd();
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJumpImmediately = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }

    private void jumpWhenCanClick() {
        if (canJumpImmediately) {
            jumpMainActivity();
        } else {
            canJumpImmediately = true;
        }
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jumpMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        overridePendingTransition(0, 0);

        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
