package com.gt.demo;

import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.gt.sdk.GTAdSdk;
import com.gt.sdk.api.GtCustomController;
import com.gt.sdk.api.GtInitCallback;
import com.gt.sdk.api.GtSdkConfig;

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

        initSDK();
    }

    private void initSDK() {

        Map<String, Object> customData = new HashMap<>();
        customData.put("custom_key", "custom_value");

        GTAdSdk.getInstance().init(this, new GtSdkConfig.Builder()
                .appId("1105")       //必填，向广推商务获取
                .userId("")  // 用户ID，有就填
                .debugEnv(true) // 是否使用测试环境域名 请求广告，正式环境务必为false
                .showLog(true)
                .addCustomData(customData)  //自定义数据
                .customController(new GtCustomController() {
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
                    // 是否允许SDK写外部数据存储
                    @Override
                    public boolean canUseWriteExternal() {
                        return true;
                    }
                    // 是否允许SDK获取应用安装列表
                    @Override
                    public boolean canReadInstalledPackages() {
                        return true;
                    }
                    // 是否允许SDK获取Wifi状态
                    @Override
                    public boolean canUseWifiState() {
                        return true;
                    }
                    // 为SDK提供oaid
                    @Override
                    public String getOaid() {
                        return "";
                    }
                })
                .setInitCallback(new GtInitCallback() {
                    // 初始化成功回调，初始化成功后才可以加载广告
                    @Override
                    public void onSuccess() {
                        Log.d(Constants.LOG_TAG, "init--------------onSuccess-----------");
                    }

                    // 初始化失败回调
                    @Override
                    public void onFail(int code, String msg) {
                        Log.d(Constants.LOG_TAG, "init--------------onFail-----------" + code + ":" + msg);
                    }
                }).build());

        // 个性化广告开关设置
        GTAdSdk.getInstance().setPersonalizedAdvertisingOn(true);
    }

}
