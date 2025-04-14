package com.gt.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class DeviceInfoDemoActivity extends AppCompatActivity {

    private LinearLayout layout;

    private Map<String, String> mAdVersions = new LinkedHashMap<>();

    private String[] mAdNames = {"IMEI", "GAID", "OAID", "GTSDK"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        layout = findViewById(R.id.ll_view);

        initChannelVersion();

        createView();
    }

    private void createView() {
        layout.removeAllViews();
        if (mAdVersions.size() > 0) {

            for (Map.Entry<String, String> entry : mAdVersions.entrySet()) {

                String key = entry.getKey();
                String value = entry.getValue();

                LinearLayout ll = new LinearLayout(this);
                ll.setBackgroundColor(getColorRandom());
                ll.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                p.setMargins(10, 10, 10, 10);
                ll.setLayoutParams(p);

                TextView tvA = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 7;
                layoutParams.gravity = Gravity.CENTER;
                tvA.setPadding(5, 5, 5, 5);
                tvA.setLayoutParams(layoutParams);
                tvA.setGravity(Gravity.CENTER);
                tvA.setText(key + ":");
                tvA.setTextSize(15);
                ll.addView(tvA);

                TextView tvB = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.weight = 3;
                tvB.setLayoutParams(params);
                tvB.setPadding(10, 10, 10, 10);
                layoutParams.gravity = Gravity.CENTER;
                tvB.setGravity(Gravity.CENTER);
                tvB.setText(value);
                tvB.post(new Runnable() {
                    @Override
                    public void run() {
                        tvB.setTextIsSelectable(true);
                    }
                });
                tvB.setTextSize(15);
                ll.addView(tvB);

                layout.addView(ll);
            }
        }
    }

    private int getColorRandom() {
        int a = Double.valueOf(Math.random() * 255).intValue();
        int r = Double.valueOf(Math.random() * 255).intValue();
        int g = Double.valueOf(Math.random() * 255).intValue();
        int b = Double.valueOf(Math.random() * 255).intValue();
        return Color.argb(a, r, g, b);
    }


    private void initChannelVersion() {
        mAdVersions.clear();
        for (int i = 0; i < mAdNames.length; i++) {
            String mAdName = mAdNames[i];
            switch (mAdName) {
                case "IMEI":
                    try {
                        Class cm = Class.forName("com.gt.sdk.share.common.ClientMetadata");
                        Method getInstance = cm.getMethod("getInstance");
                        getInstance.setAccessible(true);
                        Object instance = getInstance.invoke(cm);
                        Class<?> aClass = instance.getClass();
                        Method deviceId = aClass.getMethod("getDeviceId");
                        deviceId.setAccessible(true);
                        String invoke = (String) deviceId.invoke(instance);
                        mAdVersions.put(mAdName, invoke);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "Null");
                        e.printStackTrace();
                    }
                    break;
                case "OAID":
                    try {
                        Class cm = Class.forName("com.gt.sdk.share.common.ClientMetadata");
                        Method getInstance = cm.getMethod("getInstance");
                        getInstance.setAccessible(true);
                        Object instance = getInstance.invoke(cm);
                        Class<?> aClass = instance.getClass();
                        Method deviceId = aClass.getMethod("getOAID");
                        deviceId.setAccessible(true);
                        String invoke = (String) deviceId.invoke(instance);
                        mAdVersions.put(mAdName, invoke);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "Null");
                        e.printStackTrace();
                    }
                    break;
                case "GAID":
                    try {
                        Class cm = Class.forName("com.gt.sdk.share.common.ClientMetadata");
                        Method getInstance = cm.getMethod("getInstance");
                        getInstance.setAccessible(true);
                        Object instance = getInstance.invoke(cm);
                        Class<?> aClass = instance.getClass();
                        Method deviceId = aClass.getMethod("getAdvertisingId");
                        deviceId.setAccessible(true);
                        String invoke = (String) deviceId.invoke(instance);
                        mAdVersions.put(mAdName, invoke);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "Null");
                        e.printStackTrace();
                    }
                    break;
                case "GTSDK":
                    try {
                        Class aClass = Class.forName("com.gt.sdk.GTAdSdk");
                        Method method = aClass.getMethod("getVersionName");
                        method.setAccessible(true);
                        String invoke = (String) method.invoke(aClass);
                        mAdVersions.put(mAdName, invoke);
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "NoChannel");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

}