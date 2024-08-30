package com.gt.android.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.gt.sdk.GtAdSdk;
import com.gt.sdk.api.GtCustomController;
import com.gt.sdk.api.GtInitCallback;
import com.gt.sdk.api.GtSdkConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyApplication extends MultiDexApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        initSDK();
    }

    private void initSDK() {

        Map<String, String> customData = new HashMap<>();
        customData.put("key", "value");
        GtAdSdk.sharedAds().init(this, new GtSdkConfig.Builder()
                .appId("123456") // 测试aapId，请联系快手平台申请正式AppId，必填
                .appName("appName") // 测试appName，请填写您应用的名称，非必填
                .debug(true)//开启日志
                .userId("userID")//用户ID
                .addCustomData(customData) //自定义数据
                .customController(new GtCustomController() {
                    @Override
                    public boolean canReadLocation() {
                        return super.canReadLocation();
                    }

                    @Override
                    public Location getLocation() {
                        return super.getLocation();
                    }

                    @Override
                    public boolean canUsePhoneState() {
                        return super.canUsePhoneState();
                    }

                    @Override
                    public String getImei() {
                        return super.getImei();
                    }

                    @Override
                    public boolean canUseAndroidId() {
                        return super.canUseAndroidId();
                    }

                    @Override
                    public String getAndroidId() {
                        return super.getAndroidId();
                    }

                    @Override
                    public boolean canUseWifiState() {
                        return super.canUseWifiState();
                    }

                    @Override
                    public String getMacAddress() {
                        return super.getMacAddress();
                    }

                    @Override
                    public boolean canUseWriteExternal() {
                        return super.canUseWriteExternal();
                    }

                    @Override
                    public boolean canReadInstalledPackages() {
                        return super.canReadInstalledPackages();
                    }

                    @Override
                    public List<String> getInstalledPackages() {
                        return super.getInstalledPackages();
                    }

                    @Override
                    public String getOaid() {
                        return super.getOaid();
                    }
                })
                .setInitCallback(new GtInitCallback() {
                    @Override
                    public void onSuccess() {
                        // 启动成功后再加载广告
                        Log.d(Constants.TAG, "--------------onSuccess-----------");
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        // sdk初始化失败
                        Log.d(Constants.TAG, "--------------onFail-----------" + code + ":" + msg);
                    }
                }).build());

        GtAdSdk.sharedAds().setPersonalizedAdvertisingOn(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);

    }

    @SuppressLint("MissingPermission")
    private Location getAppLocation() {
        Location lastLocation = null;

        try {
            if (this.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED || this.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                // Get lat, long failFrom any GPS information that might be currently
                // available
                LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                for (String provider_name : lm.getProviders(true)) {
                    Location l = lm.getLastKnownLocation(provider_name);
                    if (l == null) {
                        continue;
                    }

                    if (lastLocation == null) {
                        lastLocation = l;
                    } else {
                        if (l.getTime() > 0 && lastLocation.getTime() > 0) {
                            if (l.getTime() > lastLocation.getTime()) {
                                lastLocation = l;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastLocation;
    }
}
