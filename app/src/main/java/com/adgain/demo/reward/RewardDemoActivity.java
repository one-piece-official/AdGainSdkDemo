package com.adgain.demo.reward;

import static com.adgain.demo.utils.TimeUtils.getDateTimeFormat;

import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.adgain.demo.Constants;
import com.adgain.demo.R;
import com.adgain.demo.databinding.DemoActivityRewardBinding;
import com.adgain.demo.utils.UIUtil;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.RewardAd;
import com.adgain.sdk.api.RewardAdListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RewardDemoActivity extends AppCompatActivity implements RewardAdListener, View.OnClickListener {

    private final Map<Integer, RewardAd> rewardAdMap = new HashMap<>();
    private DemoActivityRewardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DemoActivityRewardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.logView.setOnLongClickListener(v -> true);

        binding.logView.setMovementMethod(ScrollingMovementMethod.getInstance());

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
                .build();

        mRewardAd = new RewardAd(adRequest, this);

        rewardAdMap.put(mRewardAd.hashCode(), mRewardAd);

        mRewardAd.loadAd();

        logMessage("loadAd reward ");

    }

    private void showAd() {
        Log.d(Constants.LOG_TAG, "reward ---------showAd---------");

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

    private void addAdButtons() {

        try {
            String codeID = Constants.REWARD_ADCOID;
            UIUtil.createAdButtonsLayout(this, "reward", codeID, binding.adButtonsLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(Constants.LOG_TAG, "-----------addAdButtons-----------");
    }

    private void cleanLog() {
        binding.logView.setText("");
    }



    private void logMessage(String message) {
        Date date = new Date();
        binding.logView.append(getDateTimeFormat().format(date) + " " + message + '\n');
    }

    @Override
    public void onRewardAdLoadSuccess() {
        Log.d(Constants.LOG_TAG, "----------onRewardAdLoadSuccess---------- " + mRewardAd.getExtraInfo() +" " );
//        mRewardAd.onRewardAdClosed();
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
    public void onRewardAdLoadError( AdError error) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdLoadError----------" + error.toString() + ":");
        logMessage("onRewardAdLoadError() called with: error = [" + error + "], adUnitID = [" + "]");
    }

    @Override
    public void onRewardAdShowError(AdError error) {
        Log.d(Constants.LOG_TAG, "----------onRewardAdShowError----------" + error.toString() + ":");
        logMessage("onRewardAdShowError() called with: error = [" + error + "], adUnitID = [" + "]");
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