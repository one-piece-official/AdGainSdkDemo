package com.gt.android.demo.natives;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gt.android.demo.Constants;
import com.gt.android.demo.view.ILoadMoreListener;
import com.gt.android.demo.view.LoadMoreRecyclerView;
import com.gt.android.demo.view.LoadMoreView;
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


public class NativeAdUnifiedRecycleActivity extends Activity {

    private static final int LIST_ITEM_COUNT = 12;

    private LoadMoreRecyclerView mListView;

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
        setContentView(R.layout.activity_native_ad_unified_recycle);
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
        mListView = (LoadMoreRecyclerView) findViewById(R.id.unified_native_ad_recycle);
        mListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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

        AdRequest adRequest = new AdRequest.Builder().setCodeId(codeId).setExtOption(options).build();

        if (nativeUnifiedAd == null) {
            nativeUnifiedAd = new NativeUnifiedAd(adRequest, new NativeAdLoadListener() {
                @Override
                public void onAdError(String codeId, AdError error) {
                    Log.d(Constants.TAG, "onAdError:" + error.toString() + ":" + codeId);
                    Toast.makeText(NativeAdUnifiedRecycleActivity.this, "onAdError:" + error.toString(), Toast.LENGTH_SHORT).show();
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

    private static class MyAdapter extends RecyclerView.Adapter {

        private static final int FOOTER_VIEW_COUNT = 1;

        private static final int ITEM_VIEW_TYPE_LOAD_MORE = -1;
        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_UNIFIED_AD = 1;

        private List<NativeAdData> mData;

        private Activity mActivity;

        public MyAdapter(Activity activity, List<NativeAdData> data) {
            this.mActivity = activity;
            this.mData = data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case ITEM_VIEW_TYPE_LOAD_MORE:
                    return new LoadMoreViewHolder(new LoadMoreView(mActivity));
                case ITEM_VIEW_TYPE_UNIFIED_AD:
                    return new UnifiedAdViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.listitem_ad_native, parent, false));
                default:
                    return new NormalViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.listitem_normal, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UnifiedAdViewHolder) {
                NativeAdData nativeAdData = mData.get(position);
                final UnifiedAdViewHolder adViewHolder = (UnifiedAdViewHolder) holder;
                View view = getNativeAdView(nativeAdData, adViewHolder);
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
            } else if (holder instanceof NormalViewHolder) {
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
                normalViewHolder.idle.setText("Recycler item " + position);
                holder.itemView.setBackgroundColor(getColorRandom());
            } else if (holder instanceof LoadMoreViewHolder) {
                LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
                loadMoreViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        private int getColorRandom() {
            int a = Double.valueOf(Math.random() * 255).intValue();
            int r = Double.valueOf(Math.random() * 255).intValue();
            int g = Double.valueOf(Math.random() * 255).intValue();
            int b = Double.valueOf(Math.random() * 255).intValue();
            return Color.argb(a, r, g, b);
        }

        private View getNativeAdView(NativeAdData nativeAdData, UnifiedAdViewHolder adViewHolder) {

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
                    Log.d(Constants.TAG, "----------onAdRenderFail----------:" + error.toString());
                }
            });
            return adView;
        }

        @Override
        public int getItemCount() {
            int count = mData == null ? 0 : mData.size();
            return count + FOOTER_VIEW_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            if (mData != null) {
                int count = mData.size();
                if (position >= count) {
                    return ITEM_VIEW_TYPE_LOAD_MORE;
                } else {
                    NativeAdData ad = mData.get(position);
                    if (ad == null) {
                        return ITEM_VIEW_TYPE_NORMAL;
                    } else {
                        return ITEM_VIEW_TYPE_UNIFIED_AD;
                    }
                }
            }
            return super.getItemViewType(position);
        }

        private static class ExpressAdViewHolder extends AdViewHolder {
            public ExpressAdViewHolder(View itemView) {
                super(itemView);
            }
        }

        private static class UnifiedAdViewHolder extends AdViewHolder {
            //媒体自渲染的View
            NativeDemoRender adRender;

            public UnifiedAdViewHolder(View itemView) {
                super(itemView);
                adRender = new NativeDemoRender(itemView.getContext());
            }
        }

        private static class AdViewHolder extends RecyclerView.ViewHolder {

            FrameLayout adContainer;

            public AdViewHolder(View itemView) {
                super(itemView);
                adContainer = (FrameLayout) itemView.findViewById(R.id.iv_list_item_container);
            }
        }

        private static class NormalViewHolder extends RecyclerView.ViewHolder {
            TextView idle;

            public NormalViewHolder(View itemView) {
                super(itemView);
                idle = (TextView) itemView.findViewById(R.id.text_idle);
            }
        }

        private static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;
            ProgressBar mProgressBar;

            public LoadMoreViewHolder(View itemView) {
                super(itemView);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                mTextView = (TextView) itemView.findViewById(R.id.tv_load_more_tip);
                mProgressBar = (ProgressBar) itemView.findViewById(R.id.pb_load_more_progress);
            }
        }
    }
}