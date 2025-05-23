package com.adgain.demo.natives;


import static com.adgain.demo.utils.TimeUtils.getDateTimeFormat;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.adgain.demo.Constants;
import com.adgain.demo.R;
import com.adgain.demo.databinding.DemoActivityNativeAdSimpleBinding;
import com.adgain.demo.natives.renders.NativeDemoRender;
import com.adgain.demo.utils.UIUtil;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdLoadListener;
import com.adgain.sdk.api.NativeUnifiedAd;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Native AD 简单接入示例
 */
public class NativeAdSimpleDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private NativeUnifiedAd nativeAd;

    private List<NativeAdData> currentAdDataList;

    private DemoActivityNativeAdSimpleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DemoActivityNativeAdSimpleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.logView.setOnLongClickListener(v -> true);

        binding.logView.setMovementMethod(ScrollingMovementMethod.getInstance());

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
                        Log.d(Constants.LOG_TAG, "onAdLoad   adDataList = " + adDataList);
                        currentAdDataList = adDataList;
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
            binding.adContainer.removeAllViews();
            binding.adContainer.addView(view);

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

    private void updateAdButtons() {
        try {
            String codeId = Constants.NATIVE_ADCOID;
            UIUtil.createAdButtonsLayout(this, "native", codeId, binding.adButtonsLayout, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void cleanLog() {
        binding.logView.setText("");
    }

    private void logMessage(String message) {
        Date date = new Date();
        binding.logView.append(getDateTimeFormat().format(date) + " " + message + '\n');
    }

}