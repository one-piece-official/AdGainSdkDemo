package com.adgain.sdk.ui;

import static com.adgain.sdk.utils.TimeUtils.getDateTimeFormat;

import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.adgain.demo.android.R;
import com.adgain.sdk.Constants;
import com.adgain.sdk.utils.UIUtil;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.RewardAd;
import com.adgain.sdk.api.RewardAdListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 激励视频广告
 */
public class RewardActivity extends AppCompatActivity implements RewardAdListener, View.OnClickListener {

    private final Map<Integer, RewardAd> rewardAdMap = new HashMap<>();
    private LinearLayout adButtonsLayout;
    private TextView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_reward);

        adButtonsLayout = findViewById(R.id.ad_buttons_layout);
        logView = findViewById(R.id.logView);

        logView.setOnLongClickListener(v -> true);
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        addAdButtons();
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String text = (String) button.getText();
        String codeID = text.substring(text.indexOf("-") + 1);
        Log.d(Constants.LOG_TAG, "reward ---------onClick---------" + text);

        if (text.startsWith("reward LOAD-")) {
            loadAd(codeID);
        } else {
            showAd();
        }
    }

    RewardAd mRewardAd;

    private void loadAd(String codeID) {
        Log.d(Constants.LOG_TAG, "reward ---------loadAd---------");

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", "");

        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(codeID)
                .setExtOption(options)
                .setVideoMute(false)
                .build();

        mRewardAd = new RewardAd(adRequest, this);

        rewardAdMap.put(mRewardAd.hashCode(), mRewardAd);

        mRewardAd.loadAd();
        logMessage("loadAd reward ");

    }

    private void showAd() {
        Log.d(Constants.LOG_TAG, "reward ---------showAd---------");

        if (mRewardAd != null && mRewardAd.isReady()) {
            mRewardAd.showAd(this);
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

    private void addAdButtons() {

        try {
            String codeID = Constants.REWARD_ADCOID;
            UIUtil.createAdButtonsLayout(this, "reward", codeID, adButtonsLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(Constants.LOG_TAG, "-----------addAdButtons-----------");
    }

    private void cleanLog() {
        logView.setText("");
    }

    private void logMessage(String message) {
        try {
            Date date = new Date();
            logView.append(getDateTimeFormat().format(date) + " " + message + '\n');
        } catch (Exception e) {
        }
    }

    @Override
    public void onRewardAdLoadSuccess() {
        Log.d(Constants.LOG_TAG, "----------onRewardAdLoadSuccess---- price = " + mRewardAd.getBidPrice() + "   " + mRewardAd.getExtraInfo() + " ");
        logMessage("onRewardAdLoadSuccess ");
    }

    @Override
    public void onRewardAdLoadCached() {
        Log.d(Constants.LOG_TAG, "----------onRewardAdLoadCached----------");
        logMessage("onRewardAdLoadCached ");
    }

    @Override
    public void onRewardAdShow() {
        Log.d(Constants.LOG_TAG, "----------onRewardAdShow----------");
        logMessage("onRewardAdShow ");
    }

    @Override
    public void onRewardAdPlayStart() {
        Log.d(Constants.LOG_TAG, "----------onRewardAdPlayStart----------");
        logMessage("onRewardAdPlayStart ");
    }

    @Override
    public void onRewardAdPlayEnd() {
        Log.d(Constants.LOG_TAG, "----------onRewardAdPlayEnd----------");
        logMessage("onRewardAdPlayEnd ");
    }

    @Override
    public void onRewardAdClick() {
        Log.d(Constants.LOG_TAG, "----------onRewardAdClick----------");
        logMessage("onRewardAdClick ");
    }

    @Override
    public void onRewardAdClosed() {
        Log.d(Constants.LOG_TAG, "----------onRewardAdClosed----------");
        logMessage("onRewardAdClosed ");
    }

    @Override
    public void onRewardAdLoadError(AdError error) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdLoadError----------" + error.toString() + ":");
        logMessage("onRewardAdLoadError() called with: error = [" + error + "]");
    }

    @Override
    public void onRewardAdShowError(AdError error) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdShowError----------" + error.toString() + ":");
        logMessage("onRewardAdShowError() called with: error = [" + error + "]");
    }

    @Override
    public void onRewardVerify() {
        Log.d(Constants.LOG_TAG, "----------onReward----------");
        logMessage("onReward ");
    }

    @Override
    public void onAdSkip() {

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