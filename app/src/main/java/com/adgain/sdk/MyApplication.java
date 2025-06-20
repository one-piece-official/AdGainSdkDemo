package com.adgain.sdk;

import static com.adgain.sdk.Constants.APP_ID;
import static com.adgain.sdk.Constants.LOG_TAG;

import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import com.adgain.sdk.api.AdGainSdkConfig;
import com.adgain.sdk.api.CustomController;
import com.adgain.sdk.api.InitCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
//        initSDK();
    }

    private void initSDK() {

        // 个性化广告开关设置
        AdGainSdk.getInstance().setPersonalizedAdvertisingOn(true);

        Map<String, Object> customData = new HashMap<>();
        customData.put("custom_key", "custom_value");
        AdGainSdk.getInstance().init(this, new AdGainSdkConfig.Builder()
                .appId(APP_ID)
                .userId("")  // 用户ID，有就填
                .showLog(true)
                .addCustomData(customData)  //自定义数据
                .customController(new CustomController() {

                    // 是否允许SDK使用AndoridId
                    @Override
                    public boolean canUseAndroidId() {
                        return false;
                    }

                    @Override
                    public String getImei() { // 传Android系统低版本获取到的android，可选
                        return "";
                    }

                    public List<String> getInstallPackages() { // 回传检测到的app安装列表的包名（大型APP、可选）
                        List<String> test = new ArrayList<>();
                        test.add("com.t1.aa");
                        test.add("com.t2.bb");
                        return test;
                    }

                    @Override
                    public String getAndroidId() {
                        return "";
                    }// 传Android系统低版本获取到的android，可选

                    // 为SDK提供oaid
                    @Override
                    public String getOaid() {
                        return "";
                    }// 传通过信通院oaid SDK获取到的oaid值，APP内部已获取到必传
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
    }

}
