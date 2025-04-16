package com.gt.demo.natives;


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

import com.gt.demo.Constants;
import com.gt.demo.R;
import com.gt.sdk.api.AdError;
import com.gt.sdk.api.AdRequest;
import com.gt.sdk.api.NativeAdData;
import com.gt.sdk.api.NativeAdEventListener;
import com.gt.sdk.api.NativeAdLoadListener;
import com.gt.sdk.api.NativeUnifiedAd;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NativeAdUnifiedDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewGroup adContainer;

    private TextView logTextView;

    private LinearLayout IdLayout;

    private NativeUnifiedAd nativeAd;

    private List<NativeAdData> currentAdDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_native_ad_unified);

        IdLayout = this.findViewById(R.id.ll_placement);

        adContainer = findViewById(R.id.native_ad_container);

        logTextView = this.findViewById(R.id.logView);

        logTextView.setOnLongClickListener(v -> true);

        logTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        WebView.setWebContentsDebuggingEnabled(true);

        updatePlacement();
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String text = (String) button.getText();
        String adUnitID = text.substring(text.indexOf("-") + 1);

        Log.d(Constants.LOG_TAG, "---------onClick---------" + text);

        if (text.startsWith("native LOAD-")) {
            loadAd(adUnitID);

        } else {
            showAd(adUnitID);
        }
    }

    private void loadAd(String adUnitID) {

        Log.d(Constants.LOG_TAG, (nativeAd == null) + " native ---------loadAd---------" + adUnitID);

        if (null == nativeAd) {
            Map<String, Object> options = new HashMap<>();
            options.put("test_extra_key", "test_extra_value");
            AdRequest adRequest = new AdRequest
                    .Builder()
                    .setAdUnitID(adUnitID) // 设置广告位id
                    .setExtOption(options) // 自定义参数
                    .build();
            // 创建广告对象
            nativeAd = new NativeUnifiedAd(adRequest, new NativeAdLoadListener() {
                @Override
                public void onAdError(String adUnitID, AdError error) {
                    Log.d(Constants.LOG_TAG, "----------onAdError----------:" + error.toString() + ":" + adUnitID);
                    logMessage("onAdError() called with: error = [" + error + "], adUnitID = [" + adUnitID + "]");
                }

                @Override
                public void onAdLoad(String adUnitID, List<NativeAdData> adDataList) {
                    logMessage("onAdLoad [ " + adUnitID + " ]  ");

                    if (adDataList != null && !adDataList.isEmpty()) {
                        Log.d(Constants.LOG_TAG, "onAdLoad [ " + adUnitID + " ]  adUnitID = " + adUnitID + "   adDataList = " + adDataList);
                        currentAdDataList = adDataList;
                    }
                }
            });
        }
        // 请求广告
        nativeAd.loadAd();
        logMessage("loadAd [ " + adUnitID + " ]");
    }

    private void showAd(final String adUnitID) {
        Log.d(Constants.LOG_TAG, "---------showAd---------" + adUnitID);

        List<NativeAdData> unifiedADDataList = currentAdDataList;

        if (unifiedADDataList != null && !unifiedADDataList.isEmpty()) {

            NativeAdData nativeAdData = unifiedADDataList.get(0);

            View view = buildView(nativeAdData);
            // 媒体最终将要展示广告的容器
            if (adContainer != null) {
                adContainer.removeAllViews();
                adContainer.addView(view);
            }

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

    private View buildView(NativeAdData nativeAdData) {

        //设置广告交互监听
        nativeAdData.setDislikeInteractionCallback(this, new NativeAdData.DislikeInteractionCallback() {

            @Override
            public void onShow() {
                Log.d(Constants.LOG_TAG, "----------onShow----------");
            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                Log.d(Constants.LOG_TAG, "----------onSelected----------:" + position + ":" + value + ":" + enforce);
                if (adContainer != null) {
                    adContainer.removeAllViews();
                }
                logMessage("onAdClose()");
            }

            @Override
            public void onCancel() {
                Log.d(Constants.LOG_TAG, "----------onCancel----------");
            }
        });

        //媒体自渲染的View
        NativeDemoRender adRender = new NativeDemoRender(this);

        return adRender.renderAdView(nativeAdData, new NativeAdEventListener() {

            @Override
            public void onAdExposed() {
                Log.d(Constants.LOG_TAG, "----------onAdExposed----------");
                logMessage("onAdExposed()");
            }

            @Override
            public void onAdClicked() {
                Log.d(Constants.LOG_TAG, "----------onAdClicked----------");
                logMessage("onAdClicked()");
            }

            @Override
            public void onAdRenderFail(AdError error) {
                Log.d(Constants.LOG_TAG, "----------onAdRenderFail----------" + error.toString());
                logMessage("onAdRenderFail() called with: error = [" + error + "]");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 原生请求广告对象的销毁
        if (nativeAd != null) {
            nativeAd.destroyAd();
            nativeAd = null;
        }
    }

    private void updatePlacement() {

        try {
            String adUnitID = Constants.NATIVE_ADUNITID;

            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            Button loadB = new Button(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 5, 0, 5);
            layoutParams.weight = 1;
            loadB.setLayoutParams(layoutParams);
            loadB.setOnClickListener(this);
            loadB.setText("native LOAD-" + adUnitID);
            loadB.setTextSize(12);
            ll.addView(loadB);

            Button playB = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 5, 0, 5);
            params.weight = 1;
            playB.setLayoutParams(params);
            playB.setOnClickListener(this);
            playB.setText("native SHOW-" + adUnitID);
            playB.setTextSize(12);
            ll.addView(playB);

            IdLayout.addView(ll);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SimpleDateFormat dateFormat = null;

    private static SimpleDateFormat getDateTimeFormat() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss SSS", Locale.CHINA);
        }
        return dateFormat;
    }

    private void cleanLog() {
        logTextView.setText("");
    }

    private void logMessage(String message) {
        Date date = new Date();
        logTextView.append(getDateTimeFormat().format(date) + " " + message + '\n');
    }

}