package com.adgain.demo.natives;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.adgain.demo.Constants;
import com.adgain.demo.R;
import com.adgain.demo.databinding.DemoActivityNativeAdFeedBinding;
import com.adgain.demo.databinding.FeedAdItemViewBinding;
import com.adgain.demo.databinding.FeedNormalItemViewBinding;
import com.adgain.demo.natives.renders.NativeDemoFeedRender;
import com.adgain.demo.utils.UIUtil;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdRequest;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdLoadListener;
import com.adgain.sdk.api.NativeUnifiedAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdFeedDemoActivity extends AppCompatActivity {
    private static final int[] covers = {
    };
    private static final int[] videos = {};

    private static final int ITEM_VIEW_TYPE_NORMAL = 0;
    private static final int ITEM_VIEW_TYPE_AD = 1;

    private DemoActivityNativeAdFeedBinding binding;
    private MyDrawAdapter recyclerAdapter;
    private final List<DrawItemData> mDrawList = new ArrayList<>();

    private NativeUnifiedAd nativeAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DemoActivityNativeAdFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UIUtil.hideBottomUIMenu(this);
        initView();
        initDefaultDate();
        loadDrawAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDrawList.clear();
        if (nativeAd != null) {
            nativeAd.destroyAd();
            nativeAd = null;
        }
    }

    private void initDefaultDate() {
        for (int i = 0; i < videos.length; i++) {
            DrawItemData.VideoData normalVideo = new DrawItemData.VideoData(videos[i], covers[i]);
            mDrawList.add(new DrawItemData(normalVideo, null));
        }
        recyclerAdapter.notifyDataSetChanged();
    }

    /**
     * 加载广告
     */
    private void loadDrawAd() {
        Log.d(Constants.LOG_TAG, "-----------loadDrawAd-----------");
        Map<String, Object> options = new HashMap<>();
        options.put("user_id", "123456");
        String adUnitID = Constants.NATIVE_ADCOID;
        AdRequest adRequest = new AdRequest.Builder().setCodeId(adUnitID).setExtOption(options).build();

        if (nativeAd == null) {
            nativeAd = new NativeUnifiedAd(adRequest, new NativeAdLoadListener() {
                @Override
                public void onAdError(AdError error) {
                    Log.d(Constants.LOG_TAG, "----------onAdError----------:" + error.toString() + ":" + adUnitID);
                    Toast.makeText(NativeAdFeedDemoActivity.this, "onError:" + error.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLoad(List<NativeAdData> adDataList) {

                    if (adDataList != null && !adDataList.isEmpty()) {

                        for (final NativeAdData adData : adDataList) {
                            Log.d(Constants.LOG_TAG, adDataList.size() + "----------onAdLoad----------");
                            // 将数据随机插入到 feed 流中
                            int randomIdx = (int) (Math.random() * mDrawList.size());
                            if (randomIdx == 0) {
                                randomIdx++;
                            }
                            mDrawList.add(randomIdx, new DrawItemData(null, adData));
                        }

                        recyclerAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
        nativeAd.loadAd();
    }

    private void initView() {
        RecyclerView recyclerView = binding.recyclerview;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new MyDrawAdapter(this, mDrawList);
        recyclerView.setAdapter(recyclerAdapter);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);

        final int[] scrolledBy = new int[]{0};

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                View snapView = pagerSnapHelper.findSnapView(layoutManager);
                if (snapView != null) {
                    int position = layoutManager.getPosition(snapView);
                    // 当前显示第 position 个条目
                    if (!mDrawList.get(position).isAd()) {
                        RecyclerView.ViewHolder holder = binding.recyclerview.findViewHolderForAdapterPosition(position);
                        if (holder instanceof NormalViewHolder) {
                            ((NormalViewHolder) holder).playVideo();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                scrolledBy[0] = dy;
            }
        });
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                if (recyclerView.getChildCount() == 1) {
                    if (!mDrawList.get(0).isAd()) {
                        RecyclerView.ViewHolder holder = binding.recyclerview.findViewHolderForLayoutPosition(0);
                        if (holder instanceof NormalViewHolder) {
                            ((NormalViewHolder) holder).playVideo();
                        }
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                int releaseIndex = scrolledBy[0] >= 0 ? 0 : 1;
                int position = layoutManager.getPosition(view);
                if (!mDrawList.get(position).isAd()) {
                    RecyclerView.ViewHolder holder = binding.recyclerview.findViewHolderForAdapterPosition(position);
                    if (holder instanceof NormalViewHolder) {
                        ((NormalViewHolder) holder).releaseVideo();
                    }
                }
            }
        });
    }

    private static class DrawItemData {
        private VideoData videoData;
        private NativeAdData nativeAdData;

        DrawItemData(VideoData videoData, NativeAdData nativeAdData) {
            this.videoData = videoData;
            this.nativeAdData = nativeAdData;
        }

        boolean isAd() {
            return nativeAdData != null;
        }

        private static class VideoData {
            public int videoRes;
            public int coverRes;

            VideoData(int videoRes, int coverRes) {
                this.videoRes = videoRes;
                this.coverRes = coverRes;
            }
        }
    }

    private static class MyDrawAdapter extends RecyclerView.Adapter {

        private Activity activity;
        private List<DrawItemData> dataList;

        MyDrawAdapter(Activity context, List<DrawItemData> dataList) {
            this.activity = context;
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_VIEW_TYPE_AD) {
                return AdViewViewHolder.createHolder(activity.getLayoutInflater(), parent);
            }
            return NormalViewHolder.createHolder(activity.getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            DrawItemData itemData = dataList.get(position);
            if (itemData == null) {
                return;
            }
            if (viewHolder instanceof NormalViewHolder) {
                ((NormalViewHolder) viewHolder).bind(itemData);
            } else if (viewHolder instanceof AdViewViewHolder) {
                ((AdViewViewHolder) viewHolder).bind(itemData);
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        @Override
        public int getItemViewType(int position) {
            DrawItemData item = dataList.get(position);
            if (item.isAd()) {
                return ITEM_VIEW_TYPE_AD;
            } else {
                return ITEM_VIEW_TYPE_NORMAL;
            }
        }

    }

    private static class AdViewViewHolder extends RecyclerView.ViewHolder {

        public static AdViewViewHolder createHolder(LayoutInflater inflater, ViewGroup parent) {
            return new AdViewViewHolder(FeedAdItemViewBinding.inflate(inflater, parent, false));
        }

        FeedAdItemViewBinding itemBinding;
        //媒体自渲染的View
        NativeDemoFeedRender adRender;

        public AdViewViewHolder(FeedAdItemViewBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
            adRender = new NativeDemoFeedRender(itemView.getContext());
        }

        public void bind(DrawItemData data) {
            View view = getNativeAdView(data.nativeAdData);
            //添加进容器
            itemBinding.videoContainer.removeAllViews();
            if (view != null) {
                UIUtil.removeViewFromParent(view);
                itemBinding.videoContainer.addView(view);
            }
        }

        private View getNativeAdView(NativeAdData nativeAdData) {
            // 广告交互监听
            View adView = adRender.renderAdView(nativeAdData, new NativeAdEventListener() {
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
                    Log.d(Constants.LOG_TAG, "----------onAdRenderFail----------:" + error);
                }
            });
            return adView;
        }
    }

    private static class NormalViewHolder extends RecyclerView.ViewHolder {

        public static NormalViewHolder createHolder(LayoutInflater inflater, ViewGroup parent) {
            return new NormalViewHolder(FeedNormalItemViewBinding.inflate(inflater, parent, false));
        }

        private FeedNormalItemViewBinding itemBinding;

        NormalViewHolder(FeedNormalItemViewBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
        }

        public void bind(DrawItemData data) {
            String pkgName = itemBinding.videoView.getContext().getPackageName();
            itemBinding.videoView.setVideoURI(Uri.parse("android.resource://" + pkgName + "/" + data.videoData.videoRes));
            itemBinding.videoCover.setImageResource(data.videoData.coverRes);
            Glide.with(itemBinding.videoView.getContext()).load(R.drawable.avatar).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(itemBinding.authorAvatar);
            itemBinding.videoCover.setVisibility(View.VISIBLE);
        }

        public void playVideo() {
            if (!itemBinding.videoView.isPlaying()) {
                itemBinding.videoView.start();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                itemBinding.videoView.setOnInfoListener((mp, what, extra) -> {
                    itemBinding.videoCover.animate().alpha(0).setDuration(200).start();
                    return false;
                });
            } else {
                itemBinding.videoCover.animate().alpha(0).setDuration(200).start();
            }
        }

        public void releaseVideo() {
            itemBinding.videoView.stopPlayback();
            itemBinding.videoCover.animate().alpha(1).start();
        }
    }

}