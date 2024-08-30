package com.gt.android.demo.natives;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gt.android.demo.Constants;
import com.gt.android.demo.view.ILoadMoreListener;
import com.gt.android.demo.view.LoadMoreListView;
import com.gt.sdk.api.AdError;
import com.gt.sdk.api.AdRequest;
import com.gt.sdk.api.NativeAdData;
import com.gt.sdk.api.NativeAdEventListener;
import com.gt.sdk.api.NativeAdLoadListener;
import com.gt.sdk.api.NativeUnifiedAd;
import com.gt.android.demo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdUnifiedListActivity extends Activity {

    private static final int LIST_ITEM_COUNT = 12;
    private LoadMoreListView mListView;
    private MyAdapter myAdapter;
    private NativeUnifiedAd nativeUnifiedAd;
    private int userID = 0;
    private String codeId;

    private List<NativeAdData> mData;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private int adWidth; // 广告宽高

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_unified_list);
        getExtraInfo();
        initListView();
        adWidth = screenWidthAsIntDips(this) - 20;//减20因为容器有个margin 10dp//340
    }

    private void getExtraInfo() {
        Intent intent = getIntent();
        codeId = intent.getStringExtra("placementId");
        if (TextUtils.isEmpty(codeId)) {
            String[] stringArray = getResources().getStringArray(R.array.native_id_value);
            codeId = stringArray[0];
        }
    }

    public static int screenWidthAsIntDips(Context context) {
        int pixels = context.getResources().getDisplayMetrics().widthPixels;
        float density = context.getResources().getDisplayMetrics().density;
        return (int) ((pixels / density) + 0.5f);
    }


    private void initListView() {
        mListView = (LoadMoreListView) findViewById(R.id.unified_native_ad_list);
        mData = new ArrayList<>();
        myAdapter = new MyAdapter(this, mData);
        mListView.setAdapter(myAdapter);
        mListView.setLoadMoreListener(new ILoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadListAd();
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadListAd();
            }
        }, 500);
    }

    /**
     * 加载feed广告
     */
    private void loadListAd() {
        Log.d(Constants.TAG, adWidth + "-----------loadListAd-----------" + codeId);
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
                    Toast.makeText(NativeAdUnifiedListActivity.this, "onAdError:" + error.toString(), Toast.LENGTH_SHORT).show();
                    if (mListView != null) {
                        mListView.setLoadingFinish();
                    }
                }

                @Override
                public void onAdLoad(String codeId, List<NativeAdData> adDataList) {
                    if (mListView != null) {
                        mListView.setLoadingFinish();
                    }

                    if (adDataList != null && !adDataList.isEmpty()) {
                        Log.d(Constants.TAG, "onAdLoad:" + adDataList.size());
                        for (final NativeAdData adData : adDataList) {

                            for (int i = 0; i < LIST_ITEM_COUNT; i++) {
                                mData.add(null);
                            }

                            int count = mData.size();
                            mData.set(count - 1, adData);
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
        if (mData != null) {
            for (NativeAdData ad : mData) {
                if (ad != null) {
                    ad.destroy();
                }
            }
            mData = null;
        }
        if (nativeUnifiedAd != null) {
            nativeUnifiedAd.destroyAd();
        }
    }

    private static class MyAdapter extends BaseAdapter {

        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_UNIFIED_AD = 1;
        private List<NativeAdData> mData;
        private Activity mActivity;

        public MyAdapter(Activity activity, List<NativeAdData> data) {
            this.mActivity = activity;
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size(); // for test
        }

        @Override
        public NativeAdData getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //信息流广告的样式，有大图、小图、组图和视频，通过ad.getImageMode()来判断
        @Override
        public int getItemViewType(int position) {
            NativeAdData ad = getItem(position);
            if (ad == null) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else {
                return ITEM_VIEW_TYPE_UNIFIED_AD;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NativeAdData ad = getItem(position);
            switch (getItemViewType(position)) {
                case ITEM_VIEW_TYPE_UNIFIED_AD:
                    return getUnifiedADView(convertView, parent, ad);
                default:
                    return getNormalView(convertView, parent, position);
            }
        }

        //渲染视频广告，以视频广告为例，以下说明
        @SuppressWarnings("RedundantCast")
        private View getUnifiedADView(View convertView, ViewGroup viewGroup, @NonNull final NativeAdData ad) {
            final UnifiedAdViewHolder adViewHolder;
            try {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.listitem_ad_native, viewGroup, false);
                    adViewHolder = new UnifiedAdViewHolder(convertView);
                    convertView.setTag(adViewHolder);
                } else {
                    adViewHolder = (UnifiedAdViewHolder) convertView.getTag();
                }

                //绑定广告数据、设置交互回调
                View view = getNativeAdView(ad, adViewHolder);

                //添加进容器
                if (adViewHolder.adContainer != null) {
                    adViewHolder.adContainer.removeAllViews();
                    if (view != null) {
                        ViewGroup parent = (ViewGroup) view.getParent();
                        if (parent != null) {
                            parent.removeView(view);
                        }
                        adViewHolder.adContainer.addView(view);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }


        private View getNativeAdView(NativeAdData nativeAdData, UnifiedAdViewHolder adViewHolder) {
            //设置dislike弹窗
            nativeAdData.setDislikeInteractionCallback(mActivity, new NativeAdData.DislikeInteractionCallback() {
                @Override
                public void onShow() {
                    Log.d(Constants.TAG, "----------onShow----------");
                }

                @Override
                public void onSelected(int position, String value, boolean enforce) {
                    Log.d(Constants.TAG, "----------onSelected----------:" + position + ":" + value + ":" + enforce);
                    //用户选择不喜欢原因后，移除广告展示
                    mData.remove(nativeAdData);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancel() {
                    Log.d(Constants.TAG, "----------onCancel----------");
                }
            });
            //设置广告交互监听
            View adView = adViewHolder.adRender.renderAdView(nativeAdData, new NativeAdEventListener() {
                @Override
                public void onAdExposed() {
                    Log.d(Constants.TAG, "----------onAdExposed----------");
                }

                @Override
                public void onAdClicked() {
                    Log.d(Constants.TAG, "----------onAdClicked----------");
                }

                @Override
                public void onAdRenderFail(AdError error) {
                    Log.d(Constants.TAG, "----------onAdRenderFail----------" + error.toString());
                }
            });
            return adView;
        }

        /**
         * 非广告list
         *
         * @param convertView
         * @param parent
         * @param position
         * @return
         */
        @SuppressWarnings("RedundantCast")
        @SuppressLint("SetTextI18n")
        private View getNormalView(View convertView, ViewGroup parent, int position) {
            NormalViewHolder normalViewHolder;
            if (convertView == null) {
                normalViewHolder = new NormalViewHolder();
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.listitem_normal, parent, false);
                normalViewHolder.idle = (TextView) convertView.findViewById(R.id.text_idle);
                convertView.setTag(normalViewHolder);
            } else {
                normalViewHolder = (NormalViewHolder) convertView.getTag();
            }
            normalViewHolder.idle.setText("ListView item " + position);
            return convertView;
        }

        private static class UnifiedAdViewHolder extends AdViewHolder {
            //媒体自渲染的View
            NativeDemoRender adRender;

            public UnifiedAdViewHolder(View convertView) {
                super(convertView);
                adRender = new NativeDemoRender(convertView.getContext());
            }
        }

        private static class AdViewHolder {

            FrameLayout adContainer;

            public AdViewHolder(View convertView) {
                adContainer = (FrameLayout) convertView.findViewById(R.id.iv_list_item_container);
            }
        }

        private static class NormalViewHolder {
            TextView idle;
        }
    }
}