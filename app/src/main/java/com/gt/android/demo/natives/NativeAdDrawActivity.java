package com.gt.android.demo.natives;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import com.gt.android.demo.Constants;
import com.gt.sdk.api.AdError;
import com.gt.sdk.api.AdRequest;
import com.gt.sdk.api.NativeAdData;
import com.gt.sdk.api.NativeAdEventListener;
import com.gt.sdk.api.NativeAdLoadListener;
import com.gt.sdk.api.NativeUnifiedAd;
import com.gt.android.demo.R;
import com.gt.android.demo.utils.UIUtils;
import com.gt.android.demo.widget.OnViewPagerListener;
import com.gt.android.demo.widget.ViewPagerLayoutManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdDrawActivity extends Activity {
    private static final String TAG = "NativeAdDrawActivity";
    private String userID = "0";
    private String codeId;
    private RecyclerView mRecyclerView;
    private ViewPagerLayoutManager mLayoutManager;
    private DrawRecyclerAdapter mRecyclerAdapter;
    private List<TestItem> mDrawList = new ArrayList<>();
    private int adWidth, adHeight;
    private int[] images = {R.mipmap.video11, R.mipmap.video12, R.mipmap.video13, R.mipmap.video14, R.mipmap.video_2};
    private int[] videos = {R.raw.video11, R.raw.video12, R.raw.video13, R.raw.video14, R.raw.video_2};
    private NativeUnifiedAd nativeUnifiedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_draw);
        getExtraInfo();
        initView();
        initListener();
        initDefaultDate();
        loadDrawAd();
    }

    private void getExtraInfo() {
        Intent intent = getIntent();
        codeId = intent.getStringExtra("placementId");
        if (TextUtils.isEmpty(codeId)) {
            String[] stringArray = getResources().getStringArray(R.array.native_draw_id_value);
            codeId = stringArray[1];
        }
        adWidth = (int) UIUtils.getScreenWidthDp(this);
        adHeight = (int) UIUtils.getHeight(this);
        Log.d(Constants.TAG, adWidth + "---------screenWidthAsIntDips---------" + adHeight);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLayoutManager != null) {
            mLayoutManager.setOnViewPagerListener(null);
        }
    }

    private void initDefaultDate() {
        for (int i = 0; i < 5; i++) {
            TestItem.NormalVideo normalVideo = new TestItem.NormalVideo(videos[i], images[i]);
            mDrawList.add(new TestItem(normalVideo, null));
        }
        mRecyclerAdapter.notifyDataSetChanged();
    }

    /**
     * 加载Draw广告
     */
    private void loadDrawAd() {
        Log.d(Constants.TAG, "-----------loadDrawAd-----------");
        Map<String, String> options = new HashMap<>();
        options.put("user_id", userID);
        AdRequest adRequest = new AdRequest.Builder().setCodeId(codeId).setExtOption(options).build();
        if (nativeUnifiedAd == null) {
            nativeUnifiedAd = new NativeUnifiedAd(adRequest, new NativeAdLoadListener() {
                @Override
                public void onAdError(String codeId, AdError error) {
                    Log.d(Constants.TAG, "----------onAdError----------:" + error.toString() + ":" + codeId);
                    Toast.makeText(NativeAdDrawActivity.this, "onAdError:" + error.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLoad(String codeId, List<NativeAdData> adDataList) {

                    if (adDataList != null && !adDataList.isEmpty()) {
                        for (final NativeAdData adData : adDataList) {
                            Log.d(Constants.TAG, "----------onAdLoad----------:" + adDataList.size());
                            int random = (int) (Math.random() * 100);
                            int index = random % mDrawList.size();
                            if (index == 0) {
                                index++;
                            }
                            mDrawList.add(index, new TestItem(null, adData));
                        }

                        mRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        nativeUnifiedAd.loadAd();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL, false);
        mRecyclerAdapter = new DrawRecyclerAdapter(this, mDrawList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    private void initListener() {
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                Log.d(TAG, "初始化完成");
                if (!mDrawList.get(0).isAdVideoView()) {
                    playVideo();
                }
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.d(TAG, "释放位置:" + position + " 下一页:" + isNext);
                int index = isNext ? 0 : 1;
                if (!mDrawList.get(position).isAdVideoView()) {
                    releaseVideo(index);
                }
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.d(TAG, "选中位置:" + position + "  是否是滑动到底部:" + isBottom);
                if (!mDrawList.get(position).isAdVideoView()) {
                    playVideo();
                }
            }
        });
    }

    private void playVideo() {
        View itemView = mRecyclerView.getChildAt(0);
        if (itemView != null) {
            VideoView videoView = itemView.findViewById(R.id.video_view);
            final ImageView imgThumb = itemView.findViewById(R.id.video_thumb);

            if (videoView == null) {
                return;
            }

            if (!videoView.isPlaying()) {
                videoView.start();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        imgThumb.animate().alpha(0).setDuration(200).start();
                        return false;
                    }
                });
            } else {
                imgThumb.animate().alpha(0).setDuration(200).start();
            }
        }
    }

    private void releaseVideo(int index) {
        View itemView = mRecyclerView.getChildAt(index);
        if (itemView != null) {
            VideoView videoView = itemView.findViewById(R.id.video_view);
            if (videoView == null) {
                return;
            }
            ImageView imgThumb = itemView.findViewById(R.id.video_thumb);
            videoView.stopPlayback();
            imgThumb.animate().alpha(1).start();
        }
    }

    private static class DrawRecyclerAdapter extends RecyclerView.Adapter {
        private Activity mContext;
        private List<TestItem> mDataList;

        DrawRecyclerAdapter(Activity context, List<TestItem> dataList) {
            this.mContext = context;
            this.mDataList = dataList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ItemViewType.ITEM_VIEW_TYPE_UNIFIED_AD:
                    return new UnifiedAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.draw_draw_item_view, parent, false));
                case ItemViewType.ITEM_VIEW_TYPE_NORMAL:
                default:
                    return new NormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.draw_normal_item_view, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            TestItem item = mDataList.get(position);
            if (item == null) {
                return;
            }
            if (viewHolder instanceof NormalViewHolder) {
                NormalViewHolder normalViewHolder = (NormalViewHolder) viewHolder;
                normalViewHolder.videoView.setVideoURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + item.normalVideo.videoId));
                normalViewHolder.videoThumb.setImageResource(item.normalVideo.imgId);
                Glide.with(mContext).load(R.drawable.header_icon).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(normalViewHolder.authorIcon);
                normalViewHolder.videoThumb.setVisibility(View.VISIBLE);

            } else if (viewHolder instanceof UnifiedAdViewHolder) {
                UnifiedAdViewHolder unifiedAdViewHolder = (UnifiedAdViewHolder) viewHolder;
                View view = getNativeAdView(item.nativeAdData, unifiedAdViewHolder);
                //添加进容器
                if (unifiedAdViewHolder.adContainer != null) {
                    unifiedAdViewHolder.adContainer.removeAllViews();
                    if (view != null) {
                        ViewGroup parent = (ViewGroup) view.getParent();
                        if (parent != null) {
                            parent.removeView(view);
                        }
                        unifiedAdViewHolder.adContainer.addView(view);
                    }
                }
            }
        }

        private View getNativeAdView(NativeAdData nativeAdData, UnifiedAdViewHolder adViewHolder) {
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
            return mDataList.size();
        }

        @Override
        public int getItemViewType(int position) {
            TestItem item = mDataList.get(position);
            if (item.isAdVideoView()) {
                return ItemViewType.ITEM_VIEW_TYPE_UNIFIED_AD;
            } else {
                return ItemViewType.ITEM_VIEW_TYPE_NORMAL;
            }
        }

        @IntDef({ItemViewType.ITEM_VIEW_TYPE_NORMAL, ItemViewType.ITEM_VIEW_TYPE_UNIFIED_AD})
        @Retention(RetentionPolicy.SOURCE)
        @Target(ElementType.PARAMETER)
        @interface ItemViewType {
            int ITEM_VIEW_TYPE_NORMAL = 0;
            int ITEM_VIEW_TYPE_UNIFIED_AD = 1;
        }
    }


    private static class ExpressAdViewHolder extends AdViewHolder {
        public ExpressAdViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class UnifiedAdViewHolder extends AdViewHolder {
        //媒体自渲染的View
        NativeDemoDrawRender adRender;

        public UnifiedAdViewHolder(View itemView) {
            super(itemView);
            adRender = new NativeDemoDrawRender(itemView.getContext());
        }
    }

    private static class AdViewHolder extends RecyclerView.ViewHolder {

        FrameLayout adContainer;

        public AdViewHolder(View itemView) {
            super(itemView);
            adContainer = (FrameLayout) itemView.findViewById(R.id.video_container);
        }
    }

    private static class NormalViewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private ImageView videoThumb;
        private ImageView authorIcon;

        NormalViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.video_view);
            videoThumb = itemView.findViewById(R.id.video_thumb);
            authorIcon = itemView.findViewById(R.id.author_icon);
        }
    }

    private static class TestItem {
        private NormalVideo normalVideo;
        private NativeAdData nativeAdData;

        TestItem(NormalVideo normalVideo, NativeAdData nativeAdData) {
            this.normalVideo = normalVideo;
            this.nativeAdData = nativeAdData;
        }

        boolean isAdVideoView() {
            return nativeAdData != null;
        }

        private static class NormalVideo {
            public int videoId;
            public int imgId;

            NormalVideo(int videoId, int imgId) {
                this.videoId = videoId;
                this.imgId = imgId;
            }
        }
    }

}