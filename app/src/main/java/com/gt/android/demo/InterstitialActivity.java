package com.gt.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.gt.sdk.api.AdError;
import com.gt.sdk.api.AdRequest;
import com.gt.sdk.api.InterstitialAd;
import com.gt.sdk.api.InterstitialAdListener;
import com.gt.android.demo.log.CallBackInfo;
import com.gt.android.demo.log.CallBackItem;
import com.gt.android.demo.log.ExpandAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterstitialActivity extends Activity implements InterstitialAdListener, AdapterView.OnItemSelectedListener {

    private InterstitialAd interstitialAd;
    private String codeId;
    private String userID = "123456789";
    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;

    private int selectedId = 0;

    private ListView listView;
    private ExpandAdapter adapter;
    private List<CallBackItem> callBackDataList = new ArrayList<>();

    private void initCallBack() {
        resetCallBackData();
        listView = findViewById(R.id.callback_lv);
        adapter = new ExpandAdapter(this, callBackDataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(Constants.TAG, "------onItemClick------" + position);
                CallBackItem callItem = callBackDataList.get(position);
                if (callItem != null) {
                    if (callItem.is_expand()) {
                        callItem.set_expand(false);
                    } else {
                        callItem.set_expand(true);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        spinner = findViewById(R.id.id_spinner);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.interstitial_adapter));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        WebView.setWebContentsDebuggingEnabled(true);

        initCallBack();

    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_load_ad:
                resetCallBackData();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                updatePlacementId();
                Map<String, String> options = new HashMap<>();
                options.put("user_id", String.valueOf(userID));
                AdRequest adRequest = new AdRequest.Builder().setCodeId(codeId).setExtOption(options).build();

                interstitialAd = new InterstitialAd(adRequest);
                interstitialAd.setInterstitialAdListener(this);
                interstitialAd.loadAd();
                break;
            case R.id.bt_show_ad:
                if (interstitialAd != null && interstitialAd.isReady()) {
                    interstitialAd.showAd(this);
                } else {
                    Log.d(Constants.TAG, "------Ad is not Ready------");
                }
                break;
        }
    }

    private void updatePlacementId() {
        String[] stringArray = getResources().getStringArray(R.array.interstitial_id_value);
        codeId = stringArray[selectedId];
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (interstitialAd != null) {
            interstitialAd.destroyAd();
            interstitialAd = null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(Constants.TAG, "------onItemSelected------" + position);
        selectedId = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(Constants.TAG, "------onNothingSelected------");
    }

    @Override
    public void onInterstitialAdLoadSuccess(final String codeId) {
        Log.d(Constants.TAG, "------onInterstitialAdLoadSuccess------" + codeId);
        logCallBack("onInterstitialAdLoadSuccess", "");
    }

    @Override
    public void onInterstitialAdCacheSuccess(String codeId) {
        Log.d(Constants.TAG, "------onInterstitialAdCacheSuccess------" + codeId);
        logCallBack("onInterstitialAdCacheSuccess", "");
    }

    @Override
    public void onInterstitialAdPlay(String codeId) {
        Log.d(Constants.TAG, "------onInterstitialAdPlay------" + codeId);
        logCallBack("onInterstitialAdPlay", "");
    }

    @Override
    public void onInterstitialAdPLayEnd(String codeId) {
        Log.d(Constants.TAG, "------onInterstitialAdPLayEnd------" + codeId);
        logCallBack("onInterstitialAdPLayEnd", "");
    }

    @Override
    public void onInterstitialAdClick(String codeId) {
        Log.d(Constants.TAG, "------onInterstitialAdClick------" + codeId);
        logCallBack("onInterstitialAdClick", "");
    }

    @Override
    public void onInterstitialAdSkip(String codeId) {
        Log.d(Constants.TAG, "------onInterstitialAdSkip------" + codeId);
        logCallBack("onInterstitialAdSkip", "");
    }

    @Override
    public void onInterstitialAdClosed(String codeId) {
        Log.d(Constants.TAG, "------onInterstitialAdClosed------" + codeId);
        logCallBack("onInterstitialAdClosed", "");
    }

    @Override
    public void onInterstitialAdLoadError(String codeId, AdError error) {
        Log.d(Constants.TAG, "------onInterstitialAdLoadError------" + error.toString() + ":" + codeId);
        logCallBack("onInterstitialAdLoadError", error.toString());
    }

    @Override
    public void onInterstitialAdShowError(String codeId, AdError error) {
        Log.d(Constants.TAG, "------onInterstitialAdShowError------" + error.toString() + ":" + codeId);
        logCallBack("onInterstitialAdShowError", error.toString());
    }

    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.INTERSTITIAL_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.INTERSTITIAL_CALLBACK[i], "", false, false));
        }
    }

    private void logCallBack(String call, String child) {
        for (int i = 0; i < callBackDataList.size(); i++) {
            CallBackItem callItem = callBackDataList.get(i);
            if (callItem.getText().equals(call)) {
                callItem.set_callback(true);
                if (!TextUtils.isEmpty(child)) {
                    callItem.setChild_text(child);
                }
                break;
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}