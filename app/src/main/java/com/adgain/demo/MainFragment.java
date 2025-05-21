package com.adgain.demo;

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
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adgain.demo.databinding.MainFragmentBinding;
import com.adgain.demo.interstitial.InterstitialDemoActivity;
import com.adgain.demo.mini.UriSchemeListActivity;
import com.adgain.demo.natives.NativeAdDemoActivity;
import com.adgain.demo.reward.RewardDemoActivity;
import com.adgain.demo.utils.UIUtil;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.SplashAd;
import com.adgain.sdk.api.SplashAdListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainFragment extends Fragment implements SplashAdListener {
    private static final String ARG_KEY_LOGS = "logs";

    public static MainFragment newInstance(String[] logs) {
        MainFragment ret = new MainFragment();
        Bundle arguments = new Bundle();
        arguments.putStringArray(ARG_KEY_LOGS, logs);
        ret.setArguments(arguments);
        return ret;
    }

    private String[] mLogs;
    private Activity mActivity;
    String splashAdUnitId = Constants.SPLASH_ADCOID;
    private ViewGroup splashLY;
    MainFragmentBinding binding;

    private void initViewGroup(Activity activity) {
        splashLY = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        viewGroup.addView(splashLY, layoutParams);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            setLogs(args.getStringArray(ARG_KEY_LOGS));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.splashButtonLoad.setOnClickListener(v -> LoadSplashAd());
        binding.splashButtonShow.setOnClickListener(v -> showSplashAd());
        binding.splashButtonReady.setOnClickListener(v -> {
            boolean ready = splashAd != null && splashAd.isReady();
            Log.d(Constants.LOG_TAG, "---------ready---------" + ready);
            logMessage("splashAd ready = " + ready);
        });
        binding.cleanLogButton.setOnClickListener(v -> cleanLog());
        binding.logView.setOnLongClickListener(v -> true);
        binding.logView.setMovementMethod(ScrollingMovementMethod.getInstance());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        if (mLogs != null) {
            Arrays.stream(mLogs).forEach(this::logMessage);
        }
        
        // 添加微信小程序示例入口
        binding.wechatMiniprogramButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getMyActivity(), WeChatMiniProgramDemoActivity.class);
            Intent intent = new Intent(getMyActivity(), UriSchemeListActivity.class);
            startActivity(intent);
        });
        
        // jump map
        Map.of(
                binding.interstitialButton, InterstitialDemoActivity.class,
                binding.rewardButton, RewardDemoActivity.class,
                binding.nativeButton, NativeAdDemoActivity.class,
                binding.deviceInfo, DeviceInfoDemoActivity.class
        ).entrySet().forEach(entry -> {
            entry.getKey().setOnClickListener(v -> {
                Intent intent = new Intent(getMyActivity(), entry.getValue());
                startActivity(intent);
            });
        });
    }

    SplashAd splashAd;

    private void LoadSplashAd() {
        initViewGroup(getMyActivity());
        Log.d(Constants.LOG_TAG, (splashAd == null) + "---------LoadSplashAd---------" + splashAdUnitId);

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", "");
        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(splashAdUnitId)
                .setWidth(UIUtil.getScreenWidthInPx(getMyActivity()))
                .setHeight(UIUtil.getRealHeightInPx(getMyActivity()))
                .setExtOption(options)
                .build();

        splashAd = new SplashAd(adRequest, this, 5 * 1000);

        Log.d(Constants.LOG_TAG, "------------start--------loadAd-------" + System.currentTimeMillis());
        splashAd.loadAd();

        logMessage("start load splash " + splashAdUnitId);

    }

    private void showSplashAd() {
        Log.d(Constants.LOG_TAG, "---------showAd---------" + splashAdUnitId);
        // 展示前先判断广告是否ready
        if (splashAd != null && splashAd.isReady()) {
            splashAd.showAd(splashLY);
        } else {
            logMessage("splashAd is not ready or splashAd is null");
        }
    }

    private void setLogs(String[] logs) {
        mLogs = logs;
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
        binding.logView.setText("");
    }

    private static SimpleDateFormat dateFormat = null;

    private static SimpleDateFormat getDateTimeFormat() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss SSS", Locale.CHINA);
        }
        return dateFormat;
    }

    private void logMessage(String message) {
        Date date = new Date();
        binding.logView.append(getDateTimeFormat().format(date) + " " + message + '\n');
    }

    @Override
    public void onAdLoadSuccess() {
        Log.d(Constants.LOG_TAG, "----------onSplashAdLoadSuccess----------");
        logMessage("onSplashAdLoadSuccess");
    }

    @Override
    public void onAdCacheSuccess() {
        Log.d(Constants.LOG_TAG, "----------onAdCacheSuccess----------" + " " + Thread.currentThread());

    }

    @Override
    public void onSplashAdLoadFail(AdError error) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdLoadFail----------" + error.toString() + ":");
        logMessage("onSplashAdFailToLoad:" + error + " adUnitID: ");
        if (splashLY != null) {
            splashLY.removeAllViews();
            splashLY.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSplashAdShow() {
        Log.d(Constants.LOG_TAG, "----------onSplashAdShow----------");
        logMessage("onSplashAdShow");
        UIUtil.hideBottomUIMenu(getMyActivity());
    }

    @Override
    public void onSplashAdShowError(AdError error) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdShowError----------" + error.toString() + ":");
        logMessage("onSplashAdShowError:" + error + " adUnitID: ");
        if (splashLY != null) {
            splashLY.removeAllViews();
            splashLY.setVisibility(View.GONE);
        }
        UIUtil.showBottomUIMenu(getMyActivity());
    }

    @Override
    public void onSplashAdClick() {
        Log.d(Constants.LOG_TAG, "----------onSplashAdClicked----------");
        logMessage("onSplashAdClicked");
    }

    @Override
    public void onSplashAdClose(boolean isSkip) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdClose---main" + ":" + splashLY + " " + isSkip);
        logMessage("onSplashAdClose");
        if (splashLY != null) {
            splashLY.removeAllViews();
            splashLY.setVisibility(View.GONE);
        }
        UIUtil.showBottomUIMenu(getMyActivity());
    }
}
