package com.gt.demo;

import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gt.sdk.api.AdError;
import com.gt.sdk.api.AdRequest;
import com.gt.sdk.api.GTAdInfo;
import com.gt.sdk.api.RewardAd;
import com.gt.sdk.api.RewardAdListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RewardDemoActivity extends AppCompatActivity implements RewardAdListener, View.OnClickListener {

    private TextView logTextView;
    private LinearLayout IdLayout;

    private final Map<Integer, RewardAd> rewardAdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        IdLayout = this.findViewById(R.id.ll_placement);

        logTextView = this.findViewById(R.id.logView);

        logTextView.setOnLongClickListener(v -> true);

        logTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        updatePlacement();
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String text = (String) button.getText();
        String adUnitID = text.substring(text.indexOf("-") + 1);
        Log.d(Constants.LOG_TAG, "reward ---------onClick---------" + text);

        if (text.startsWith("reward LOAD-")) {
            loadAd(adUnitID);

        } else {
            showAd(adUnitID);
        }
    }

    RewardAd mRewardAd;

    private void loadAd(String adunitid) {
        Log.d(Constants.LOG_TAG, "reward ---------loadAd---------" + adunitid);

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", "");

        AdRequest adRequest = new AdRequest.Builder()
                .setAdUnitID(adunitid)
                .setExtOption(options)
                .build();

        mRewardAd = new RewardAd(adRequest, this);

        rewardAdMap.put(mRewardAd.hashCode(), mRewardAd);

        mRewardAd.loadAd();

        logMessage("loadAd reward [ " + adunitid + " ]");

    }

    private void showAd(String adUnitID) {
        Log.d(Constants.LOG_TAG, "reward ---------showAd---------" + adUnitID);

        if (mRewardAd != null && mRewardAd.isReady()) {
            mRewardAd.showAd(null);
        } else {
            logMessage("Ad is not Ready");
            Log.d(Constants.LOG_TAG, "reward --------请先加载广告--------");
        }
    }

    public void buttonClick(View view) {
        if (view.getId() == R.id.cleanLog_button) {
            cleanLog();
        }
    }

    private void updatePlacement() {

        try {
            String adSlotId = Constants.REWARD_ADUNITID;
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            Button loadB = new Button(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 5, 0, 5);
            layoutParams.weight = 1;
            loadB.setLayoutParams(layoutParams);
            loadB.setOnClickListener(this);
            loadB.setText("reward LOAD-" + adSlotId);
            loadB.setTextSize(12);
            ll.addView(loadB);

            Button playB = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 5, 0, 5);
            params.weight = 1;
            playB.setLayoutParams(params);
            playB.setOnClickListener(this);
            playB.setText("reward Show-" + adSlotId);
            playB.setTextSize(12);
            ll.addView(playB);
            IdLayout.addView(ll);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(Constants.LOG_TAG,  "-----------updatePlacement-----------");
    }

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
    public void onRewardAdLoadSuccess(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdLoadSuccess----------" + adUnitID +"   adInfo = " + adInfo);
        logMessage("onRewardAdLoadSuccess [ " + adUnitID + " ]");
    }

    @Override
    public void onRewardAdLoadCached(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdLoadCached----------" + adUnitID +"   adInfo = " + adInfo);
        logMessage("onRewardAdLoadCached [ " + adUnitID + " ]");
    }

    @Override
    public void onRewardAdShow(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdShow----------" + adUnitID +"   adInfo = " + adInfo);
        logMessage("onRewardAdShow [ " + adUnitID + " ]");
    }

    @Override
    public void onRewardAdPlayStart(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdPlayStart----------" + adUnitID +"   adInfo = " + adInfo);
        logMessage("onRewardAdPlayStart [ " + adUnitID + " ]");
    }

    @Override
    public void onRewardAdPLayEnd(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdPLayEnd----------" + adUnitID +"   adInfo = " + adInfo);
        logMessage("onRewardAdPLayEnd [ " + adUnitID + " ]");
    }

    @Override
    public void onRewardAdClick(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdClick----------" + adUnitID +"   adInfo = " + adInfo);
        logMessage("onRewardAdClick [ " + adUnitID + " ]");
    }

    @Override
    public void onRewardAdClosed(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdClosed----------" + adUnitID +"   adInfo = " + adInfo);
        logMessage("onRewardAdClosed [ " + adUnitID + " ]");
    }

    @Override
    public void onRewardAdLoadError(String adUnitID, AdError error) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdLoadError----------" + error.toString() + ":" + adUnitID);
        logMessage("onRewardAdLoadError() called with: error = [" + error + "], adUnitID = [" + adUnitID + "]");
    }

    @Override
    public void onRewardAdShowError(String adUnitID, AdError error) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdShowError----------" + error.toString() + ":" + adUnitID);
        logMessage("onRewardAdShowError() called with: error = [" + error + "], adUnitID = [" + adUnitID + "]");
    }

    @Override
    public void onReward(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onReward----------" +"   adInfo = " + adInfo);
        logMessage("onReward [ " + adUnitID + " ]");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (RewardAd rewardAd : rewardAdMap.values()) {
            if (rewardAd != null) {
                Log.d(Constants.LOG_TAG, "reward onDestroy == " + rewardAd);
                rewardAd.destroyAd();
            }
        }
    }

}