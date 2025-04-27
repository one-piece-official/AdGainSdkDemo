package com.gt.demo.splash;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.gt.demo.Constants;
import com.gt.demo.MainActivity;
import com.gt.demo.databinding.DemoActivitySplashBinding;
import com.gt.demo.utils.UIUtil;
import com.gt.sdk.GTAdSdk;
import com.gt.sdk.api.AdError;
import com.gt.sdk.api.AdRequest;
import com.gt.sdk.api.GTAdInfo;
import com.gt.sdk.api.SplashAd;
import com.gt.sdk.api.SplashAdListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SplashDemoActivity extends AppCompatActivity {

    /**
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。
     * 此时需要增加 isForeground 判断当前 Activity 是否在前台。
     * 如果在前台，需要跳转时可以直接跳转，否则等到 回到前台时再跳转。
     */
    private boolean isForeground = false;
    private SplashAd splashAd;
    private ArrayList<String> logs;
    private ViewGroup mViewGroup;

    private DemoActivitySplashBinding binding;

    private final SplashAdListener mSplashAdListener = new SplashAdListener() {

        // 开屏广告加载成功通知，加载成功后才可以展示广告
        @Override
        public void onSplashAdLoadSuccess(String adUnitID, GTAdInfo adInfo) {
            Log.d(Constants.LOG_TAG, "----------onSplashAdLoadSuccess----------" + splashAd.isReady() + ":" + adUnitID   + "  adInfo = " + adInfo);
            logs.add("onSplashAdLoadSuccess:" + splashAd.isReady());

            // 展示前先判断广告是否ready
            if (splashAd != null && splashAd.isReady()) {
                // 执行展示广告
                splashAd.showAd(mViewGroup);
            }
        }

        // 开屏广告加载失败通知
        @Override
        public void onSplashAdLoadFail(String adUnitID, AdError error) {
            Log.d(Constants.LOG_TAG, "----------onSplashAdLoadFail----------" + error.toString() + ":" + adUnitID);
            logs.add("onSplashAdLoadFail: " + error + " adUnitID: " + adUnitID);
            gotoMainActivity();
        }

        @Override
        public void onSplashAdShow(String adUnitID, GTAdInfo adInfo) {
            Log.d(Constants.LOG_TAG, "----------onSplashAdShow----------" + adUnitID  + "  adInfo = " + adInfo);
            logs.add("onSplashAdShow");
        }

        // 开屏广告展示错误
        @Override
        public void onSplashAdShowError(String adUnitID, AdError error) {
            Log.d(Constants.LOG_TAG, "----------onSplashAdShowError----------" + error.toString() + ":" + adUnitID);
            logs.add("onSplashAdShowError: " + error + " adUnitID: " + adUnitID);
            gotoMainActivity();
        }

        // 开屏广告被用户点击通知
        @Override
        public void onSplashAdClick(String adUnitID, GTAdInfo adInfo) {
            Log.d(Constants.LOG_TAG, "----------onSplashAdClick----------" + adInfo  + "  adInfo = " + adInfo);
            logs.add("onSplashAdClick");
        }

        // 开屏广告关闭通知
        @Override
        public void onSplashAdClose(String adUnitID, GTAdInfo adInfo) {
            Log.d(Constants.LOG_TAG, "----------onSplashAdClose----------" + adUnitID  + "  adInfo = " + adInfo);
            logs.add("onSplashAdClose");
            if (isForeground) {
                gotoMainActivity();
            } else {
                // 不在前台，说明打开了广告中的链接，等回到 onResume 时再跳转
                getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                        if (Lifecycle.Event.ON_RESUME == event) {
                            getLifecycle().removeObserver(this);
                            gotoMainActivity();
                        }
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getView());

        mViewGroup = getSplashContainer();

        logs = new ArrayList<>();
        logs.add("init SDK appId :" + GTAdSdk.getInstance().getAppId());

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", "");
        options.put("splash_self_key", "splash_self_value");

        // 创建ad请求
        AdRequest adRequest = new AdRequest.Builder()
                .setAdUnitID(Constants.SPLASH_ADUNITID) // 广告位ID
                .setWidth(UIUtil.getScreenWidthInPx(this)) // 设置需要的广告视图宽度
                .setHeight(getSplashHeight()) // 设置需要的广告视图高度
                .setExtOption(options) // 设置透传的自定义数据
                .setSplashAdLoadTimeoutMs(5 * 1000) // 设置开屏广告加载的超时时间（单位：ms），如果这个时间段内广告加载失败会给出超时错误回调
                .setPortrait(!(this instanceof SplashLandDemoActivity)) // 设置是否竖屏，true表示竖屏，false表示横屏
                .build();
        // 创建开屏AD API对象，监听回调在这里设置
        splashAd = new SplashAd(adRequest, mSplashAdListener);
        // 加载广告
        splashAd.loadAd();
    }

    protected View getView(){
        binding = DemoActivitySplashBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    protected ViewGroup getSplashContainer() {
        return binding.splashContainer;
    }

    protected int getSplashHeight(){
        return UIUtil.getScreenHeightInPx(this) - UIUtil.dp2px(this,100);
    }

    /**
     * 跳转到主 Activity
     */
    private void gotoMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        String[] list = logs.toArray(new String[0]);
        intent.putExtra("logs", list);
        startActivity(intent);

        overridePendingTransition(0, 0);

        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 界面销毁时，执行销毁Ad接口
        if (splashAd != null) {
            splashAd.destroyAd();
            splashAd = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
