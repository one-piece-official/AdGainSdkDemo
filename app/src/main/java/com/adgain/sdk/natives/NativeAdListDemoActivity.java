package com.adgain.sdk.natives;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Native AD 在ListView中的接入示例
 */
public class NativeAdListDemoActivity extends AppCompatActivity {

    private static final int NORMAL_ITEM_COUNT = 8;
    private MyAdapter myAdapter;
    private NativeUnifiedAd nativeUnifiedAd;
    private final List<NativeAdData> mData = new ArrayList<>();
    private boolean isLoading = false;

    private ListView listView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_native_ad_list);
        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();
    }

    private void initView() {
        myAdapter = new MyAdapter(this, mData);
        listView.setAdapter(myAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int firstVisibleItem;
            private int visibleItemCount;
            private int totalItemCount;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && !isLoading && (firstVisibleItem + visibleItemCount) >= totalItemCount) {
                    loadListAd();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.firstVisibleItem = firstVisibleItem;
                this.visibleItemCount = visibleItemCount;
                this.totalItemCount = totalItemCount;
            }
        });

        loadListAd();
    }

    private void showLoading(boolean show) {
        isLoading = show;
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void loadListAd() {
        Log.d(Constants.LOG_TAG, "-----------loadListAd-----------");
        showLoading(true);
        Map<String, Object> options = new HashMap<>();
        String adUnitID = Constants.NATIVE_ADCOID;
        AdRequest adRequest = new AdRequest.Builder().setCodeId(adUnitID).setExtOption(options).build();

        if (nativeUnifiedAd == null) {
            nativeUnifiedAd = new NativeUnifiedAd(adRequest, new NativeAdLoadListener() {
                @Override
                public void onAdError( AdError error) {
                    Log.d(Constants.LOG_TAG, "----------onAdError----------:" + error.toString() + ":" + adUnitID);
                    Toast.makeText(NativeAdListDemoActivity.this, "onAdError:" + error, Toast.LENGTH_SHORT).show();
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

    private static class MyAdapter extends BaseAdapter {

        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_AD = 1;
        private final List<NativeAdData> mData;
        private final Activity mActivity;

        public MyAdapter(Activity activity, List<NativeAdData> data) {
            this.mActivity = activity;
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public NativeAdData getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //信息流广告的样式, 通过 ad.getAdPatternType 接口判断
        @Override
        public int getItemViewType(int position) {
            NativeAdData ad = getItem(position);
            if (ad == null) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else {
                return ITEM_VIEW_TYPE_AD;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NativeAdData ad = getItem(position);
            switch (getItemViewType(position)) {
                case ITEM_VIEW_TYPE_AD:
                    return getADItemView(convertView, parent, ad);
                default:
                    return getNormalItemView(convertView, parent, position);
            }
        }

        //渲染视频广告，以视频广告为例，以下说明
        private View getADItemView(View convertView, ViewGroup viewGroup, @NonNull final NativeAdData ad) {
            final NativeAdViewHolder adViewHolder;
            try {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.listitem_ad_native, viewGroup, false);
                    adViewHolder = new NativeAdViewHolder(convertView);
                    convertView.setTag(adViewHolder);
                } else {
                    adViewHolder = (NativeAdViewHolder) convertView.getTag();
                }

                // 绑定广告数据、设置交互回调
                View view = createNativeAdView(ad, adViewHolder);
                // 添加进容器
                adViewHolder.adContainer.removeAllViews();
                if (view != null) {
                    UIUtil.removeViewFromParent(view);
                    adViewHolder.adContainer.addView(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        /**
         * 非广告list item
         *
         * @param convertView
         * @param parent
         * @param position
         * @return
         */
        @SuppressWarnings("RedundantCast")
        @SuppressLint("SetTextI18n")
        private View getNormalItemView(View convertView, ViewGroup parent, int position) {
            NormalViewHolder normalViewHolder;
            if (convertView != null) {
                normalViewHolder = (NormalViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.listitem_normal, parent, false);
                normalViewHolder = new NormalViewHolder(convertView);
                convertView.setTag(normalViewHolder);
                convertView.setBackgroundColor(UIUtil.getARandomColor());
            }
            normalViewHolder.text.setText("ListView item " + position);
            return convertView;
        }

        private View createNativeAdView(NativeAdData nativeAdData, NativeAdViewHolder adViewHolder) {
            // 广告交互监听
            View adView = adViewHolder.adRender.renderAdView(nativeAdData, new NativeAdEventListener() {
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
                    Log.d(Constants.LOG_TAG, "----------onAdRenderFail----------" + error.toString());
                }
            });
            return adView;
        }

        private static class NativeAdViewHolder {

            FrameLayout adContainer;
            NativeDemoRender adRender;

            public NativeAdViewHolder(View itemView) {
                adContainer = itemView.findViewById(R.id.ad_container);
                adRender = new NativeDemoRender(itemView.getContext());
            }
        }


        private static class NormalViewHolder {
            TextView text;

            NormalViewHolder(View itemView) {
                text = itemView.findViewById(R.id.text);
            }
        }
    }
}