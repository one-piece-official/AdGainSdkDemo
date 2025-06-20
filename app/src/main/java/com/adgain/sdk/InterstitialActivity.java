package com.adgain.sdk;

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
import com.adgain.sdk.utils.UIUtil;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.InterstitialAd;
import com.adgain.sdk.api.InterstitialAdListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 插屏广告
 */
public class InterstitialActivity extends AppCompatActivity implements InterstitialAdListener, View.OnClickListener {

    private final Map<Integer, InterstitialAd> interstitialAdMap = new HashMap<>();
    private LinearLayout adButtonsLayout;
    private TextView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_interstitial);

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
        String codeId = text.substring(text.indexOf("-") + 1);
        Log.d(Constants.LOG_TAG, "---------onClick---------" + text);

        if (text.startsWith("inter LOAD-")) {
            loadAd(codeId);

        } else {
            showAd();
        }
    }

    InterstitialAd mInterstitialAd;

    private void loadAd(String codeId) {
        Log.d(Constants.LOG_TAG, "interstitial ---------loadAd---------");

        Map<String, Object> options = new HashMap<>();
        options.put("inter_extra_test_key", "inter_extra_test_value");
        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(codeId)
                .setExtOption(options)
                .build();

        mInterstitialAd = new InterstitialAd(adRequest, this);

        interstitialAdMap.put(mInterstitialAd.hashCode(), mInterstitialAd);

        mInterstitialAd.loadAd();

        logMessage("loadAd ");
    }

    private void showAd() {
        Log.d(Constants.LOG_TAG, "---------showAd---------");

        if (mInterstitialAd != null && mInterstitialAd.isReady()) {
            mInterstitialAd.showAd(this);

        } else {
            logMessage("Ad is not Ready");
            Log.d(Constants.LOG_TAG, "--------请先加载广告--------");
        }
    }

    public void buttonClick(View view) {
        if (view.getId() == R.id.cleanLog_button) {
            cleanLog();
        }
    }

    private void addAdButtons() {
        try {
            String codeId = Constants.INTERSTITIAL_ADCODEID;
            UIUtil.createAdButtonsLayout(this, "inter", codeId, adButtonsLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(Constants.LOG_TAG, "-----------addAdButtons-----------");
    }

    private void cleanLog() {
        logView.setText("");
    }
    private void logMessage(String message) {
        Date date = new Date();
        logView.append(getDateTimeFormat().format(date) + " " + message + '\n');
    }

    @Override
    public void onInterstitialAdLoadSuccess() {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdLoadSuccess---------- " + mInterstitialAd.getExtraInfo());
        logMessage("onInterstitialAdLoadSuccess ");
    }

    @Override
    public void onInterstitialAdLoadCached() {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdLoadCached----------");
        logMessage("onInterstitialAdLoadCached ");
    }

    @Override
    public void onInterstitialAdShow() {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdShow----------");
        logMessage("onInterstitialAdShow ");
    }

    @Override
    public void onInterstitialAdPlayEnd() {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdPlayEnd----------");
        logMessage("onInterstitialAdPlayEnd ");
    }

    @Override
    public void onInterstitialAdClick() {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdClick----------");
        logMessage("onInterstitialAdClick ");
    }

    @Override
    public void onInterstitialAdClosed() {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdClosed----------");
        logMessage("onInterstitialAdClosed ");
    }

    @Override
    public void onInterstitialAdLoadError(AdError error) {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdLoadError----------" + error.toString() + ":");
        logMessage("onInterstitialAdLoadError() called with: error = [" + error);
    }

    @Override
    public void onInterstitialAdShowError(AdError error) {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdShowError----------" + error.toString() + ":");
        logMessage("onInterstitialAdShowError() called with: error = [" + error);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (InterstitialAd interstitialAd : interstitialAdMap.values()) {

            if (interstitialAd != null) {
                Log.d(Constants.LOG_TAG, "interstitial onDestroy == " + interstitialAd);
                interstitialAd.destroyAd();
            }
        }
    }

}