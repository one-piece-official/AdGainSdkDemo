package com.adgain.demo;

import static com.adgain.demo.Constants.LOG_TAG;

import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.adgain.sdk.AdGainSdk;
import com.adgain.sdk.api.AdGainSdkConfig;
import com.adgain.sdk.api.CustomController;
import com.adgain.sdk.api.InitCallback;

import java.util.HashMap;
import java.util.Map;


public class MyApplication extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appCatchHandler();

        initSDK();
    }

    private void initSDK() {

        Map<String, Object> customData = new HashMap<>();
        customData.put("custom_key", "custom_value");
        AdGainSdk.getInstance().init(this, new AdGainSdkConfig.Builder()
                .appId("1105_1173")       //必填，向广推商务获取
                .userId("")  // 用户ID，有就填
                .showLog(true)
                .addCustomData(customData)  //自定义数据
                .customController(new CustomController() {
                    // 是否允许SDK获取位置信息
                    @Override
                    public boolean canReadLocation() {
                        return true;
                    }

                    // 是否允许SDK获取手机状态地信息，如：imei deviceid
                    @Override
                    public boolean canUsePhoneState() {
                        return true;
                    }

                    // 是否允许SDK使用AndoridId
                    @Override
                    public boolean canUseAndroidId() {
                        return true;
                    }
                    @Override
                    public boolean canUseWifiState() {
                        return true;
                    }

                    // 为SDK提供oaid
                    @Override
                    public String getOaid() {
                        return "oaidtest";
                    }
                })
                .setInitCallback(new InitCallback() {
                    // 初始化成功回调，初始化成功后才可以加载广告
                    @Override
                    public void onSuccess() {
                        Log.d(LOG_TAG, "init--------------onSuccess-----------");
                    }

                    // 初始化失败回调
                    @Override
                    public void onFail(int code, String msg) {
                        Log.d(LOG_TAG, "init--------------onFail-----------" + code + ":" + msg);
                    }
                }).build());
//        String oaid = DeviceIdentifier.getOAID(this.getApplicationContext());

        // 个性化广告开关设置
        AdGainSdk.getInstance().setPersonalizedAdvertisingOn(true);
    }

    private void appCatchHandler(){
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.d(LOG_TAG, "appCatchHandler: uncaughtException ",throwable);
        });
    }

}
