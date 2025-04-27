package com.gt.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gt.demo.databinding.ActivityDeviceInfoBinding;
import com.gt.demo.utils.UIUtil;
import com.gt.sdk.GTAdSdk;

import java.lang.reflect.Method;
import java.util.List;

public class DeviceInfoDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDeviceInfoBinding binding = ActivityDeviceInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List.of(
                binding.imeiContainer,
                binding.gaidContainer,
                binding.oaidContainer,
                binding.sdkVersionContainer
        ).forEach(v -> v.setBackgroundColor(UIUtil.getARandomColor()));

        binding.imei.setText(getImei());
        binding.gaid.setText(getGaid());
        binding.oaid.setText(getOaid());
        binding.sdvVersion.setText(getSdkVersion());
    }


    private String getImei() {
        try {
            Class cm = Class.forName("com.gt.sdk.share.common.ClientMetadata");
            Method getInstance = cm.getMethod("getInstance");
            getInstance.setAccessible(true);
            Object instance = getInstance.invoke(cm);
            Class<?> aClass = instance.getClass();
            Method deviceId = aClass.getMethod("getDeviceId");
            deviceId.setAccessible(true);
            String invoke = (String) deviceId.invoke(instance);
            return invoke;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Null";
    }

    private String getOaid() {
        try {
            Class cm = Class.forName("com.gt.sdk.share.common.ClientMetadata");
            Method getInstance = cm.getMethod("getInstance");
            getInstance.setAccessible(true);
            Object instance = getInstance.invoke(cm);
            Class<?> aClass = instance.getClass();
            Method deviceId = aClass.getMethod("getOAID");
            deviceId.setAccessible(true);
            return (String) deviceId.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Null";
    }

    private String getGaid() {
        try {
            Class cm = Class.forName("com.gt.sdk.share.common.ClientMetadata");
            Method getInstance = cm.getMethod("getInstance");
            getInstance.setAccessible(true);
            Object instance = getInstance.invoke(cm);
            Class<?> aClass = instance.getClass();
            Method deviceId = aClass.getMethod("getAdvertisingId");
            deviceId.setAccessible(true);
            return (String) deviceId.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Null";
    }

    private String getSdkVersion() {
        return GTAdSdk.getVersionName();
    }

}