package com.adgain.sdk.natives;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adgain.sdk.Constants;
import com.adgain.demo.android.R;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.adgain.sdk.natives.renders.NativeDemoRender;
import com.adgain.sdk.utils.UIUtil;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdLoadListener;
import com.adgain.sdk.api.NativeUnifiedAd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Native AD 使用 RecyclerView 时的接入示例
 */
public class NativeAdRecycleDemoActivity extends AppCompatActivity {

    private static final int NORMAL_ITEM_COUNT = 8;

    private MyAdapter myAdapter;

    private NativeUnifiedAd nativeUnifiedAd;

    private final List<NativeAdData> mData = new ArrayList<>();

    private boolean isLoading = false;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_native_ad_recycle);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        initView();
        loadRecyclerAd();
    }

    private void initView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        myAdapter = new MyAdapter(this, mData);
        recyclerView.setAdapter(myAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    if (!isLoading && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loadRecyclerAd();
                    }
                }
            }
        });
    }

    private void showLoading(boolean show) {
        isLoading = show;
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void loadRecyclerAd() {
        Log.d(Constants.LOG_TAG, "-----------loadListAd-----------");
        showLoading(true);
        Map<String, Object> options = new HashMap<>();
        options.put("user_id", "123456");
        String adUnitID = Constants.NATIVE_ADCOID;
        AdRequest adRequest = new AdRequest.Builder().setCodeId(adUnitID).setExtOption(options).build();

        if (nativeUnifiedAd == null) {
            nativeUnifiedAd = new NativeUnifiedAd(adRequest, new NativeAdLoadListener() {
                @Override
                public void onAdError( AdError error) {
                    Log.d(Constants.LOG_TAG, "----------onAdError----------:" + error.toString() + ":" + adUnitID);
                    Toast.makeText(NativeAdRecycleDemoActivity.this, "onError:" + error.toString(), Toast.LENGTH_SHORT).show();
                    showLoading(false);
                }

                @Override
                public void onAdLoad( List<NativeAdData> adDataList) {
                    showLoading(false);

                    if (adDataList != null && !adDataList.isEmpty()) {
                        Log.d(Constants.LOG_TAG, "----------onAdLoad----------:" + adDataList.size());
                        for (final NativeAdData adData : adDataList) {
                            Collections.addAll(mData, new NativeAdData[NORMAL_ITEM_COUNT]);
                            mData.add(adData);
                            Collections.addAll(mData, new NativeAdData[NORMAL_ITEM_COUNT]);
                        }

                        myAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        nativeUnifiedAd.loadAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mData.clear();
        if (nativeUnifiedAd != null) {
            nativeUnifiedAd.destroyAd();
            nativeUnifiedAd = null;
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter {
        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_AD = 1;

        private final List<NativeAdData> mData;

        private final Activity mActivity;

        public MyAdapter(Activity activity, List<NativeAdData> data) {
            this.mActivity = activity;
            this.mData = data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(Constants.LOG_TAG, "-----------onCreateViewHolder-----------");

            if (viewType == ITEM_VIEW_TYPE_AD) {
                View view = LayoutInflater.from(mActivity).inflate(R.layout.listitem_ad_native, parent, false);
                return new AdViewHolder(view);
            }
            View view = LayoutInflater.from(mActivity).inflate(R.layout.listitem_normal, parent, false);
            return new NormalViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Log.d(Constants.LOG_TAG, "-----------onBindViewHolder-----------");

            if (holder instanceof AdViewHolder) {
                NativeAdData nativeAdData = mData.get(position);
                final AdViewHolder adViewHolder = (AdViewHolder) holder;
                View view = createNativeAdView(nativeAdData, adViewHolder);
                //添加进容器
                adViewHolder.adContainer.removeAllViews();
                if (view != null) {
                    UIUtil.removeViewFromParent(view);
                    adViewHolder.adContainer.addView(view);
                }
            } else if (holder instanceof NormalViewHolder) {
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
                normalViewHolder.text.setText("Recycler item " + position);
            }
        }


        private View createNativeAdView(NativeAdData nativeAdData, AdViewHolder adViewHolder) {

            // 广告交互监听
            return adViewHolder.adRender.renderAdView(nativeAdData, new NativeAdEventListener() {
                @Override
                public void onAdExposed() {
                    Log.d(Constants.LOG_TAG, "----------onAdExposed----------");
                }

                @Override
                public void onAdClicked() {
                    Log.d(Constants.LOG_TAG, "----------onAdClicked----------");
                }

                @Override
                public void onAdRenderFail(AdError error) {
                    Log.d(Constants.LOG_TAG, "----------onAdRenderFail----------:" + error.toString());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public int getItemViewType(int position) {
            NativeAdData ad = mData.get(position);
            if (ad == null) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else {
                return ITEM_VIEW_TYPE_AD;
            }
        }

        private static class AdViewHolder extends RecyclerView.ViewHolder {

            FrameLayout adContainer;
            //媒体自渲染的View
            NativeDemoRender adRender;

            public AdViewHolder(View itemView) {
                super(itemView);
                adContainer = itemView.findViewById(R.id.ad_container);
                adRender = new NativeDemoRender(itemView.getContext());
            }
        }

        private static class NormalViewHolder extends RecyclerView.ViewHolder {
            TextView text;

            public NormalViewHolder(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
            }
        }
    }
}