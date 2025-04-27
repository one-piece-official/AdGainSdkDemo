package com.gt.demo.interstitial;

import static com.gt.demo.Constants.PORTRAIT;

import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.gt.demo.Constants;
import com.gt.demo.R;
import com.gt.demo.databinding.DemoActivityInterstitialBinding;
import com.gt.demo.utils.UIUtil;
import com.gt.sdk.api.AdError;
import com.gt.sdk.api.AdRequest;
import com.gt.sdk.api.GTAdInfo;
import com.gt.sdk.api.InterstitialAd;
import com.gt.sdk.api.InterstitialAdListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InterstitialDemoActivity extends AppCompatActivity implements InterstitialAdListener, View.OnClickListener {

    private final Map<Integer, InterstitialAd> interstitialAdMap = new HashMap<>();
    private DemoActivityInterstitialBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DemoActivityInterstitialBinding.inflate(getLayoutInflater());
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
        String adUnitID = text.substring(text.indexOf("-") + 1);
        Log.d(Constants.LOG_TAG, "---------onClick---------" + text);

        if (text.startsWith("inter LOAD-")) {
            loadAd(adUnitID);

        } else {
            showAd(adUnitID);
        }
    }

    InterstitialAd mInterstitialAd;

    private void loadAd(String adUnitid) {
        Log.d(Constants.LOG_TAG, "interstitial ---------loadAd---------" + adUnitid);

        Map<String, Object> options = new HashMap<>();
        options.put("inter_extra_test_key", "inter_extra_test_value");
        AdRequest adRequest = new AdRequest.Builder()
                .setAdUnitID(adUnitid)
                .setExtOption(options)
                .setPortrait(PORTRAIT)
                .build();

        mInterstitialAd = new InterstitialAd(adRequest, this);

        interstitialAdMap.put(mInterstitialAd.hashCode(), mInterstitialAd);

        mInterstitialAd.loadAd();

        logMessage("loadAd [ " + adUnitid + " ]");
    }

    private void showAd(String adUnitID) {
        Log.d(Constants.LOG_TAG, "---------showAd---------" + adUnitID);

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
            String adUnitID = Constants.INTERSTITIAL_ADUNITID;
            UIUtil.createAdButtonsLayout(this, "inter", adUnitID, binding.adButtonsLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(Constants.LOG_TAG, "-----------addAdButtons-----------");
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
    public void onInterstitialAdLoadSuccess(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdLoadSuccess----------" + adUnitID + "   adInfo = " + adInfo);
        logMessage("onInterstitialAdLoadSuccess [ " + adUnitID + " ]");
    }

    @Override
    public void onInterstitialAdLoadCached(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdLoadCached----------" + adUnitID + "   adInfo = " + adInfo);
        logMessage("onInterstitialAdLoadCached [ " + adUnitID + " ]");
    }

    @Override
    public void onInterstitialAdShow(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdShow----------" + adUnitID + "   adInfo = " + adInfo);
        logMessage("onInterstitialAdShow [ " + adUnitID + " ]");
    }

    @Override
    public void onInterstitialAdPlayStart(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdPlayStart----------" + adUnitID + "   adInfo = " + adInfo);
        logMessage("onInterstitialAdPlayStart [ " + adUnitID + " ]");
    }

    @Override
    public void onInterstitialAdPLayEnd(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdPLayEnd----------" + adUnitID + "   adInfo = " + adInfo);
        logMessage("onInterstitialAdPLayEnd [ " + adUnitID + " ]");
    }

    @Override
    public void onInterstitialAdClick(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdClick----------" + adUnitID + "   adInfo = " + adInfo);
        logMessage("onInterstitialAdClick [ " + adUnitID + " ]");
    }

    @Override
    public void onInterstitialAdClosed(String adUnitID, GTAdInfo adInfo) {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdClosed----------" + adUnitID + "   adInfo = " + adInfo);
        logMessage("onInterstitialAdClosed [ " + adUnitID + " ]");
    }

    @Override
    public void onInterstitialAdLoadError(String adUnitID, AdError error) {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdLoadError----------" + error.toString() + ":" + adUnitID);
        logMessage("onInterstitialAdLoadError() called with: error = [" + error + "], adUnitID = [" + adUnitID + "]");
    }

    @Override
    public void onInterstitialAdShowError(String adUnitID, AdError error) {
        Log.d(Constants.LOG_TAG, "----------onInterstitialAdShowError----------" + error.toString() + ":" + adUnitID);
        logMessage("onInterstitialAdShowError() called with: error = [" + error + "], adUnitID = [" + adUnitID + "]");
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