package com.adgain.sdk.ui;

import static com.adgain.sdk.Constants.APP_ID;
import static com.adgain.sdk.Constants.LOG_TAG;
import static com.adgain.sdk.utils.TimeUtils.getDateTimeFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adgain.sdk.AdGainSdk;
import com.adgain.sdk.Constants;
import com.adgain.sdk.api.AdGainSdkConfig;
import com.adgain.sdk.api.CustomController;
import com.adgain.sdk.api.InitCallback;
import com.adgain.sdk.natives.NativeAdDemoActivity;
import com.adgain.demo.android.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends Fragment {
    private static final String ARG_KEY_LOGS = "logs";

    public static MainFragment newInstance(String[] logs) {
        MainFragment ret = new MainFragment();
        Bundle arguments = new Bundle();
        arguments.putStringArray(ARG_KEY_LOGS, logs);
        ret.setArguments(arguments);
        return ret;
    }

    private Activity mActivity;
    String splashAdCodeId = Constants.SPLASH_ADCOID;
    // UI components
    private Button splashButtonLoad;
    private Button cleanLogButton;
    private Button interstitialButton;
    private Button rewardButton;
    private Button nativeButton;
    private TextView logView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        cleanLogButton = view.findViewById(R.id.cleanLog_button);
        interstitialButton = view.findViewById(R.id.interstitial_button);
        rewardButton = view.findViewById(R.id.reward_button);
        nativeButton = view.findViewById(R.id.native_button);
        logView = view.findViewById(R.id.logView);
        // Set click listeners
        view.findViewById(R.id.splash_button_load).setOnClickListener(v -> loadSplashAd());
        cleanLogButton.setOnClickListener(v -> cleanLog());
        logView.setOnLongClickListener(v -> true);
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        // Set up navigation buttons
        interstitialButton.setOnClickListener(v -> {
            Intent intent = new Intent(getMyActivity(), InterstitialActivity.class);
            startActivity(intent);
        });

        rewardButton.setOnClickListener(v -> {
            Intent intent = new Intent(getMyActivity(), RewardActivity.class);
            startActivity(intent);
        });

        nativeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getMyActivity(), NativeAdDemoActivity.class);
            startActivity(intent);
        });
        view.findViewById(R.id.sdk_init).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSDK();
            }
        });
    }

    private void loadSplashAd() {
        startActivity(new Intent(getMyActivity(), SplashActivity.class));
        logMessage("start load splash " + splashAdCodeId);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;//保存Context引用
    }

    private Activity getMyActivity() {
        if (mActivity == null) {
            mActivity = getActivity();
        }
        return mActivity;
    }

    private void cleanLog() {
        logView.setText("");
    }

    private void logMessage(String message) {
        Date date = new Date();
        logView.append(getDateTimeFormat().format(date) + " " + message + '\n');
    }

    private void initSDK() {

        // 个性化广告开关设置
        AdGainSdk.getInstance().setPersonalizedAdvertisingOn(true);

        Map<String, Object> customData = new HashMap<>();
        customData.put("custom_key", "custom_value");
        AdGainSdk.getInstance().init(getActivity(), new AdGainSdkConfig.Builder()
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


                    @Override
                    public String getAndroidId() {
                        return "";
                    }// 传Android系统低版本获取到的android，可选

                    // 为SDK提供oaid
                    @Override
                    public String getOaid() {
                        return "oaid_test";
                    }// 传通过信通院oaid SDK获取到的oaid值，APP内部已获取到必传
                })
                .setInitCallback(new InitCallback() {
                    // 初始化成功回调，初始化成功后才可以加载广告
                    @Override
                    public void onSuccess() {
                        Log.d(LOG_TAG, "init-------onSuccess-----------");
                        logMessage("初始化成功");
                    }

                    // 初始化失败回调
                    @Override
                    public void onFail(int code, String msg) {
                        Log.d(LOG_TAG, "init--------------onFail-----------" + code + ":" + msg);
                        logMessage("初始化失败" + msg);
                    }
                }).build());
    }


}
