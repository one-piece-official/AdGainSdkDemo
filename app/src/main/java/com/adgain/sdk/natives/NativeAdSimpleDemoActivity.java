package com.adgain.sdk.natives;


import static com.adgain.sdk.utils.TimeUtils.getDateTimeFormat;
import static com.adgain.sdk.utils.UIUtil.dp2px;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.adgain.sdk.Constants;
import com.adgain.demo.android.R;
import com.adgain.sdk.natives.renders.NativeDemoRender;
import com.adgain.sdk.utils.UIUtil;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdLoadListener;
import com.adgain.sdk.api.NativeUnifiedAd;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Native AD 简单接入示例
 */
public class NativeAdSimpleDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private NativeUnifiedAd nativeAd;

    private List<NativeAdData> currentAdDataList;

    private LinearLayout adButtonsLayout;
    private FrameLayout adContainer;
    private TextView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_native_ad_simple);

        adButtonsLayout = findViewById(R.id.ad_buttons_layout);
        adContainer = findViewById(R.id.ad_container);
        logView = findViewById(R.id.logView);

        logView.setOnLongClickListener(v -> true);
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());

        WebView.setWebContentsDebuggingEnabled(true);

        updateAdButtons();
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String text = (String) button.getText();
        String codeId = text.substring(text.indexOf("-") + 1);

        Log.d(Constants.LOG_TAG, "---------onClick---------" + text);

        if (text.startsWith("native LOAD-")) {
            loadAd(codeId);

        } else {
            showAd(codeId);
        }
    }

    private void loadAd(String codeId) {

        Log.d(Constants.LOG_TAG, (nativeAd == null) + " native ---------loadAd---------" + codeId);

        if (null == nativeAd) {
            Map<String, Object> options = new HashMap<>();
            options.put("test_extra_key", "test_extra_value");
            AdRequest adRequest = new AdRequest.Builder()
                    .setCodeId(codeId) // 设置广告位id
                    .setWidth(UIUtil.getScreenWidthInPx(this) - dp2px(getApplicationContext(), 20))
                    .setHeight(500)
                    .setExtOption(options) // 自定义参数
                    .build();
            // 创建广告对象
            nativeAd = new NativeUnifiedAd(adRequest, new NativeAdLoadListener() {
                @Override
                public void onAdError(AdError error) {
                    Log.d(Constants.LOG_TAG, "----------onAdError----------:" + error.toString() + ":" + codeId);
                    logMessage("onAdError() called with: error = [" + error + "], codeId = [" + codeId + "]");
                }

                @Override
                public void onAdLoad(List<NativeAdData> adDataList) {
                    logMessage("onAdLoad [ " + codeId + " ]  ");
                    if (adDataList != null && !adDataList.isEmpty()) {
                        Log.d(Constants.LOG_TAG, "----Native   adDataList = " + (adDataList.get(0).getFeedView() != null));
                        currentAdDataList = adDataList;
                        NativeAdData data = adDataList.get(0);
                        if (data.getFeedView() != null) {
                            adContainer.removeAllViews();
                            data.setNativeAdEventListener(new NativeAdEventListener() {
                                @Override
                                public void onAdExposed() {
                                    Log.d(Constants.LOG_TAG, "----Native   onAdExposed ");
                                }

                                @Override
                                public void onAdClicked() {
                                    Log.d(Constants.LOG_TAG, "----Native   onAdClicked ");

                                }

                                @Override
                                public void onAdRenderFail(AdError error) {

                                }
                            });
                            adContainer.addView(data.getFeedView());
                            data.setNativeAdEventListener(listener);
                            data.setNativeAdMediaListener(new NativeAdData.NativeAdMediaListener() {
                                @Override
                                public void onVideoLoad() {
                                    Log.d(Constants.LOG_TAG, "----Native   onVideoLoad ");
                                }

                                @Override
                                public void onVideoError(AdError error) {

                                }

                                @Override
                                public void onVideoStart() {
                                    Log.d(Constants.LOG_TAG, "----Native   onVideoStart ");

                                }

                                @Override
                                public void onVideoPause() {

                                }

                                @Override
                                public void onVideoResume() {

                                }

                                @Override
                                public void onVideoCompleted() {

                                }
                            });
                        }
//                        showAd(codeId);
                    }
                }
            });
        }
        // 请求广告
        nativeAd.loadAd();
        logMessage("loadAd [ " + codeId + " ]");
    }

    private void showAd(final String codeId) {
        Log.d(Constants.LOG_TAG, "---------showAd---------" + codeId);

        List<NativeAdData> unifiedADDataList = currentAdDataList;

        if (unifiedADDataList != null && !unifiedADDataList.isEmpty()) {

            NativeAdData nativeAdData = unifiedADDataList.get(0);

            View view = buildView(nativeAdData);
            // 媒体最终将要展示广告的容器
            adContainer.removeAllViews();
            adContainer.addView(view);

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

        //媒体自渲染的View
        NativeDemoRender adRender = new NativeDemoRender(this);

        return adRender.renderAdView(nativeAdData, listener);
    }

    NativeAdEventListener listener = new NativeAdEventListener() {

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
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 原生请求广告对象的销毁
        if (nativeAd != null) {
            nativeAd.destroyAd();
            nativeAd = null;
        }
    }

    private void updateAdButtons() {
        try {
            String codeId = Constants.NATIVE_ADCOID;
            UIUtil.createAdButtonsLayout(this, "native", codeId, adButtonsLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanLog() {
        logView.setText("");
    }

    private void logMessage(String message) {
        Date date = new Date();
        logView.append(getDateTimeFormat().format(date) + " " + message + '\n');
    }

}