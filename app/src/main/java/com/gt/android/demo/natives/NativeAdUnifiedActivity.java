package com.gt.android.demo.natives;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.gt.android.demo.Constants;
import com.gt.sdk.api.AdError;
import com.gt.sdk.api.AdRequest;
import com.gt.sdk.api.NativeAdData;
import com.gt.sdk.api.NativeAdEventListener;
import com.gt.sdk.api.NativeAdLoadListener;
import com.gt.sdk.api.NativeUnifiedAd;
import com.gt.android.demo.R;
import com.gt.android.demo.log.CallBackInfo;
import com.gt.android.demo.log.CallBackItem;
import com.gt.android.demo.log.ExpandAdapter;
import com.gt.android.demo.log.MyListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdUnifiedActivity extends Activity {

    private ViewGroup adContainer;
    private NativeUnifiedAd nativeUnifiedAd;
    private int userID = 0;
    private String codeId;
    private List<NativeAdData> nativeAdDataList;

    private MyListView listView;
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

    private void getExtraInfo() {
        Intent intent = getIntent();
        codeId = intent.getStringExtra("placementId");
        if (TextUtils.isEmpty(codeId)) {
            String[] stringArray = getResources().getStringArray(R.array.native_id_value);
            codeId = stringArray[0];
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_unified);
        adContainer = findViewById(R.id.native_ad_container);
        getExtraInfo();

        initCallBack();
    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.load_native_button:
                resetCallBackData();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                //加载原生广告
                loadNativeAd();
                break;
            case R.id.show_native_button:
                //展示原生广告
                showNativeAd();
                break;
        }
    }

    public static int screenWidthAsIntDips(Context context) {
        int pixels = context.getResources().getDisplayMetrics().widthPixels;
        float density = context.getResources().getDisplayMetrics().density;
        return (int) ((pixels / density) + 0.5f);
    }

    private void loadNativeAd() {
        Log.d(Constants.TAG, "-----------loadNativeAd-----------");

        userID++;
        Map<String, String> options = new HashMap<>();
        options.put("user_id", String.valueOf(userID));

        AdRequest adRequest = new AdRequest
                .Builder()
                .setCodeId(codeId)
                .setExtOption(options)
                .build();


        if (nativeUnifiedAd == null) {
            nativeUnifiedAd = new NativeUnifiedAd(adRequest, new NativeAdLoadListener() {
                @Override
                public void onAdError(String codeId, AdError error) {
                    Log.d(Constants.TAG, "onAdError:" + error.toString() + ":" + codeId);
                    logCallBack("onError", error.toString());
                    Toast.makeText(NativeAdUnifiedActivity.this, "onAdError", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLoad(String codeId, List<NativeAdData> adDataList) {
                    Toast.makeText(NativeAdUnifiedActivity.this, "onAdLoad", Toast.LENGTH_SHORT).show();
                    logCallBack("onAdLoad", "");
                    if (adDataList != null && !adDataList.isEmpty()) {
                        Log.d(Constants.TAG, "onAdLoad:" + adDataList.size());
                        nativeAdDataList = adDataList;
                    }
                }
            });
        }

        nativeUnifiedAd.loadAd();
    }

    private void showNativeAd() {
        Log.d(Constants.TAG, "-----------showNativeAd-----------");
        if (nativeAdDataList != null && !nativeAdDataList.isEmpty()) {
            NativeAdData nativeAdData = nativeAdDataList.get(0);

            View view = buildView(nativeAdData);

            //媒体最终将要展示广告的容器
            if (adContainer != null) {
                adContainer.removeAllViews();
                adContainer.addView(view);
            }
        } else {
            Log.d(Constants.TAG, "--------请先加载广告--------");
        }
    }


    private View buildView(NativeAdData nativeAdData) {
        //设置广告交互监听
        nativeAdData.setDislikeInteractionCallback(this, new NativeAdData.DislikeInteractionCallback() {

            @Override
            public void onShow() {
                Log.d(Constants.TAG, "----------onShow----------");
            }

            @Override
            public void onSelected(int position, String value, boolean enforce) {
                Log.d(Constants.TAG, "----------onSelected----------:" + position + ":" + value + ":" + enforce);
                if (adContainer != null) {
                    adContainer.removeAllViews();
                }
            }

            @Override
            public void onCancel() {
                Log.d(Constants.TAG, "----------onCancel----------");
            }
        });
        //媒体自渲染的View
        NativeDemoRender adRender = new NativeDemoRender(this);
        View view = adRender.renderAdView(nativeAdData, new NativeAdEventListener() {
            @Override
            public void onAdExposed() {
                Log.d(Constants.TAG, "----------onAdExposed----------");
                logCallBack("onAdExposed", "");
            }

            @Override
            public void onAdClicked() {
                Log.d(Constants.TAG, "----------onAdClicked----------");
                logCallBack("onAdClicked", "");
            }

            @Override
            public void onAdRenderFail(AdError error) {
                Log.d(Constants.TAG, "----------onAdRenderFail----------:" + error.toString());
                logCallBack("onAdRenderFail", error.toString());
            }
        });
        return view;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (nativeAdDataList != null && !nativeAdDataList.isEmpty()) {
            for (NativeAdData ad : nativeAdDataList) {
                if (ad != null) {
                    ad.destroy();
                }
            }
        }
        if (nativeUnifiedAd != null) {
            nativeUnifiedAd.destroyAd();
        }
    }

    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.NATIVE_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.NATIVE_CALLBACK[i], "", false, false));
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