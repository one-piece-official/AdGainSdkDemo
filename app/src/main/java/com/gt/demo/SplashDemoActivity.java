package com.gt.demo;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.gt.demo.utils.PxUtils;
import com.gt.sdk.GTAdSdk;
import com.gt.sdk.api.AdError;
import com.gt.sdk.api.AdRequest;
import com.gt.sdk.api.GTAdInfo;
import com.gt.sdk.api.SplashAd;
import com.gt.sdk.api.SplashAdListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SplashDemoActivity extends AppCompatActivity implements SplashAdListener {

    /**
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。
     * 故此时需要增加canJumpImmediately判断。 另外，点击开屏还需要在onResume中调用jumpWhenCanClick接口。
     */
    public boolean canJumpImmediately = false;
    private SplashAd splashAd;
    private ArrayList<String> logs;
    private ViewGroup mViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mViewGroup = findViewById(R.id.splash_container);

        logs = new ArrayList<>();
        logs.add("init SDK appId :" + GTAdSdk.getInstance().getAppId());

        SharedPreferences sharedPreferences = getSharedPreferences("setting", 0);

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", "");
        options.put("splash_self_key", "splash_self_value");

        // 创建ad请求
        AdRequest adRequest = new AdRequest.Builder()
                .setAdUnitID(Constants.SPLASH_ADUNITID) // 广告位ID
                .setWidth(PxUtils.getDeviceWidthInPixel(this)) // 设置需要的广告视图宽度
                .setHeight(PxUtils.getDeviceHeightInPixel(this) - PxUtils.dpToPx(this,100)) // 设置需要的广告视图高度
                .setExtOption(options) // 设置透传的自定义数据
                .setSplashAdLoadTimeoutMs(5 * 1000) // 设置开屏广告超时时间（单位：ms），如果这个时间段内广告加载失败会给出超时错误回调
                .build();
        // 创建开屏AD API对象
        splashAd = new SplashAd(adRequest, this);
        // 执行广告加载，加载结果通过 {@link #onSplashAdLoadSuccess}  或 {@link #onSplashAdLoadFail} 给出
        splashAd.loadAd();
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
        String[] list = logs.toArray(new String[logs.size()]);
        intent.putExtra("logs", list);
        startActivity(intent);

        overridePendingTransition(0, 0);

        this.finish();
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

    @Override
    public void onSplashAdShow(String codeId, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdShow----------" + codeId  + "  adInfo = " + adInfo);
        logs.add("onSplashAdShow");
    }

    // 开屏广告加载成功通知，加载成功后才可以展示广告
    @Override
    public void onSplashAdLoadSuccess(String codeId, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdLoadSuccess----------" + splashAd.isReady() + ":" + codeId   + "  adInfo = " + adInfo);
        logs.add("onSplashAdLoadSuccess:" + splashAd.isReady());

        if (splashAd != null && splashAd.isReady()) {
            // 执行展示广告
            splashAd.showAd(mViewGroup);
        }
    }

    // 开屏广告加载失败通知
    @Override
    public void onSplashAdLoadFail(String codeId, AdError error) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdLoadFail----------" + error.toString() + ":" + codeId);
        logs.add("onSplashAdLoadFail: " + error + " placementId: " + codeId);
        jumpMainActivity();
    }

    // 开屏广告展示错误
    @Override
    public void onSplashAdShowError(String codeId, AdError error) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdShowError----------" + error.toString() + ":" + codeId);
        logs.add("onSplashAdShowError: " + error + " placementId: " + codeId);
        jumpMainActivity();
    }

    // 开屏广告被用户点击通知
    @Override
    public void onSplashAdClick(String codeId, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdClick----------" + codeId  + "  adInfo = " + adInfo);
        logs.add("onSplashAdClick");
    }

    // 开屏广告关闭通知
    @Override
    public void onSplashAdClose(String codeId, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdClose----------" + codeId  + "  adInfo = " + adInfo);
        logs.add("onSplashAdClose");
        jumpWhenCanClick();
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
