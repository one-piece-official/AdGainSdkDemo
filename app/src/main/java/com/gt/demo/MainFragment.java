package com.gt.demo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.gt.demo.natives.NativeAdDemoActivity;
import com.gt.demo.utils.PxUtils;
import com.gt.sdk.api.AdError;
import com.gt.sdk.api.AdRequest;
import com.gt.sdk.api.GTAdInfo;
import com.gt.sdk.api.SplashAd;
import com.gt.sdk.api.SplashAdListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainFragment extends Fragment implements SplashAdListener {

    private TextView logTextView;
    private String[] mLogs;
    private Activity mActivity;
    String splash_code_id = Constants.SPLASH_ADUNITID;
    private ViewGroup splashLY;

    private void initViewGroup(Activity activity) {
        splashLY = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        viewGroup.addView(splashLY, layoutParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    private void bindButton(@IdRes int id, Class clz) {
        getMyActivity().findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开屏代码位id
                if (v.getId() == R.id.splash_button_load) {
                    LoadSplashAd();
                    return;
                }

                if (v.getId() == R.id.splash_button_show) {
                    showSplashAd();
                    return;
                }

                if (v.getId() == R.id.splash_button_ready) {
                    boolean ready = splashAd != null && splashAd.isReady();

                    Log.d(Constants.LOG_TAG, "---------ready---------" + ready);
                    logMessage("splashAd ready = "+ ready);
                    return;
                }


                Intent intent = new Intent(getMyActivity(), clz);
                startActivity(intent);
            }
        });
    }

    SplashAd splashAd;
    private void LoadSplashAd() {
        initViewGroup(getMyActivity());

        Log.d(Constants.LOG_TAG, (splashAd == null) + "---------LoadSplashAd---------" + splash_code_id);

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", "");
        AdRequest adRequest = new AdRequest.Builder()
                .setAdUnitID(splash_code_id)
                .setWidth(PxUtils.getRealMetrics(getMyActivity()).widthPixels)
                .setHeight(PxUtils.getRealMetrics(getMyActivity()).heightPixels)
                .setExtOption(options)
                .setSplashAdLoadTimeoutMs(5 * 1000) // 5 seconds
                .build();

        splashAd = new SplashAd(adRequest, this);

        Log.d(Constants.LOG_TAG, "------------start--------loadAd-------" + System.currentTimeMillis());
        splashAd.loadAd();

        logMessage("start load splash " + splash_code_id);

    }

    private void showSplashAd() {
        Log.d(Constants.LOG_TAG, "---------showAd---------" + splash_code_id);
        if (splashAd != null && splashAd.isReady()) {
            splashAd.showAd(splashLY);
        } else {
            logMessage("splashAd is not ready or splashAd is null");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindButton(R.id.splash_button_load, null);
        bindButton(R.id.splash_button_ready, null);
        bindButton(R.id.splash_button_show, null);
        bindButton(R.id.interstitial_button, InterstitialDemoActivity.class);
        bindButton(R.id.reward_button, RewardDemoActivity.class);
        bindButton(R.id.native_button, NativeAdDemoActivity.class);
        bindButton(R.id.device_info, DeviceInfoDemoActivity.class);

        Button cleanLogBtn = getView().findViewById(R.id.cleanLog_button);

        logTextView = getView().findViewById(R.id.logView);

        cleanLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanLog();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        logTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        logTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        if (mLogs != null && mLogs.length > 0) {
            for (int i = 0; i < mLogs.length; i++) {
                logMessage(mLogs[i]);
            }
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("setting", 0);
    }

    public void setLogs(String[] logs) {
        mLogs = logs;
        if (mLogs != null && mLogs.length > 0 && logTextView != null) {
            for (int i = 0; i < mLogs.length; i++) {
                logMessage(mLogs[i]);
            }
        }
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

    /*private void configuration() {
        ConfigFragment configurationFragment = new ConfigFragment();
        FragmentTransaction transaction = getMyActivity().getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, configurationFragment);
        transaction.addToBackStack("configuration");
        transaction.commit();
    }*/

    private void cleanLog() {
        logTextView.setText("");
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
        logTextView.append(getDateTimeFormat().format(date) + " " + message + '\n');
    }

    @Override
    public void onSplashAdLoadSuccess(String codeId, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdLoadSuccess----------" + codeId  + "  adInfo = " + adInfo);
        logMessage("onSplashAdLoadSuccess");
    }

    @Override
    public void onSplashAdLoadFail(String codeId, AdError error) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdLoadFail----------" + error.toString() + ":" + codeId);
        logMessage("onSplashAdFailToLoad:" + error + " placementId: " + codeId);
        if (splashLY != null) {
            splashLY.removeAllViews();
            splashLY.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSplashAdShow(String codeId, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdShow----------" + codeId + "  adInfo = " + adInfo);
        logMessage("onSplashAdShow");
    }

    @Override
    public void onSplashAdShowError(String codeId, AdError error) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdShowError----------" + error.toString() + ":" + codeId);
        logMessage("onSplashAdShowError:" + error + " placementId: " + codeId);
        if (splashLY != null) {
            splashLY.removeAllViews();
            splashLY.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSplashAdClick(String codeId, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdClicked----------" + codeId  + "  adInfo = " + adInfo);
        logMessage("onSplashAdClicked");
    }

    @Override
    public void onSplashAdClose(String codeId, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onSplashAdClose----------main" + codeId + ":" + splashLY  + "  adInfo = " + adInfo);
        logMessage("onSplashAdClose");
        if (splashLY != null) {
            splashLY.removeAllViews();
            splashLY.setVisibility(View.GONE);
        }
    }
}
