package com.gt.android.demo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gt.android.demo.utils.PxUtils;
import com.gt.sdk.api.AdError;
import com.gt.sdk.api.AdRequest;
import com.gt.sdk.api.SplashAd;
import com.gt.sdk.api.SplashAdListener;
import com.gt.android.demo.log.CallBackInfo;
import com.gt.android.demo.log.CallBackItem;
import com.gt.android.demo.log.ExpandAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashAdActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;
    private String codeId;
    private ListView listView;
    private ExpandAdapter adapter;
    private ViewGroup splashLY;
    private SplashAd splashAd;

    private List<CallBackItem> callBackDataList = new ArrayList<>();

    private void initViewGroup(Activity activity) {

//        if (this.splashLY != null) {
//            if (this.splashLY.getParent() != null) {
//                ((ViewGroup) this.splashLY.getParent()).removeView(this.splashLY);
//            }
//
//            this.splashLY = null;
//        }

        splashLY = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        viewGroup.addView(splashLY, layoutParams);
    }

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
        setContentView(R.layout.activity_splash_ad);
        spinner = findViewById(R.id.id_spinner);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.splash_adapter));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        String[] stringArray = getResources().getStringArray(R.array.splash_id_value);
        codeId = stringArray[0];
        initCallBack();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] stringArray = getResources().getStringArray(R.array.splash_id_value);
        codeId = stringArray[position];
        Log.d(Constants.TAG, "------onItemSelected------" + position + ":" + codeId);
        SharedPreferences sharedPreferences = SplashAdActivity.this.getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.CONF_PLACEMENT_ID, codeId);
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(Constants.TAG, "------onNothingSelected------");
    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_load:
                resetCallBackData();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                loadSplashAd();
                break;
            case R.id.bt_show:
                showSplashAd();
                break;
        }
    }

    private void loadSplashAd() {

        initViewGroup(this);

        Map<String, String> options = new HashMap<>();
        options.put("user_id", "userId");

        AdRequest adRequest = new AdRequest.Builder()
                .setCodeId(codeId)
                .setWidth(PxUtils.getDeviceWidthInPixel(this))
                .setHeight(PxUtils.getDeviceHeightInPixel(this))
                .setExtOption(options)
                .build();

        splashAd = new SplashAd(adRequest, new SplashAdListener() {
            @Override
            public void onSplashAdLoadSuccess(String codeId) {
                logCallBack("onSplashAdLoadSuccess", "");
            }

            @Override
            public void onSplashAdLoadFail(String codeId, AdError error) {
                logCallBack("onSplashAdLoadFail", error.toString());
                if (splashLY != null) {
                    splashLY.removeAllViews();
                    splashLY.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSplashAdShow(String codeId) {
                logCallBack("onSplashAdShow", "");
            }

            @Override
            public void onSplashAdShowError(String codeId, AdError error) {
                logCallBack("onSplashAdShowError", error.toString());
                if (splashLY != null) {
                    splashLY.removeAllViews();
                    splashLY.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSplashAdClick(String codeId) {
                logCallBack("onSplashAdClick", "");
            }

            @Override
            public void onSplashAdClose(String codeId) {
                logCallBack("onSplashAdClose", "");
                if (splashLY != null) {
                    splashLY.removeAllViews();
                    splashLY.setVisibility(View.GONE);
                }
            }
        });

        splashAd.loadAd();
    }

    private void showSplashAd() {
        if (splashAd != null && splashAd.isReady()) {
            splashAd.showAd(splashLY);
        } else {
            Toast.makeText(SplashAdActivity.this, "Ad is not Ready", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.SPLASH_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.SPLASH_CALLBACK[i], "", false, false));
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