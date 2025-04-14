package com.gt.demo.natives;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gt.demo.Constants;
import com.gt.demo.R;
import com.gt.sdk.api.AdAppInfo;
import com.gt.sdk.api.AdError;
import com.gt.sdk.api.GtImage;
import com.gt.sdk.api.NativeAdData;
import com.gt.sdk.api.NativeAdEventListener;
import com.gt.sdk.api.NativeAdInteractiveType;
import com.gt.sdk.api.NativeAdPatternType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeDemoRender {

    private Context context;

    private ImageView img_logo;
    private ImageView ad_logo;

    private ImageView img_dislike;

    private TextView text_desc;

    private View mButtonsContainer;

    private Button mPlayButton;
    private Button mPauseButton;
    private Button mStopButton;

    private FrameLayout mMediaViewLayout;

    private LinearLayout privacyLayout;

    private ImageView mImagePoster;
    private LinearLayout native_3img_ad_container;

    private ImageView img_1;
    private ImageView img_2;
    private ImageView img_3;

    private TextView text_title;
    private Button mCTAButton;

    private FrameLayout shakeLayout;

    private Map<Integer, View> developViewMap = new HashMap<>();

    public NativeDemoRender(Context activity) {
        this.context = activity;
    }


    public View createView(Context context, NativeAdData adData) {
        Log.d(Constants.LOG_TAG, "---------createView----------" + adData.hashCode());

        this.context = context;

        View developView = developViewMap.get(adData.hashCode());

        if (developView == null) {
            developView = LayoutInflater.from(context).inflate(R.layout.native_ad_item_normal, null);
            developViewMap.put(adData.hashCode(), developView);
        }

        if (developView.getParent() != null) {
            ((ViewGroup) developView.getParent()).removeView(developView);
        }

        return developView;
    }

    public View renderAdView(NativeAdData adData, NativeAdEventListener nativeAdEventListener) {

        View view = createView(context, adData);
        Log.d(Constants.LOG_TAG, "renderAdView:" + adData.getTitle());

        img_logo = view.findViewById(R.id.img_logo);
        ad_logo = view.findViewById(R.id.channel_ad_logo);
        img_dislike = view.findViewById(R.id.iv_dislike);

        text_desc = view.findViewById(R.id.text_desc);

        mButtonsContainer = view.findViewById(R.id.video_btn_container);
        mPlayButton = view.findViewById(R.id.btn_play);
        mPauseButton = view.findViewById(R.id.btn_pause);
        mStopButton = view.findViewById(R.id.btn_stop);

        mMediaViewLayout = view.findViewById(R.id.media_layout);
        mImagePoster = view.findViewById(R.id.img_poster);
        shakeLayout = view.findViewById(R.id.shake_layout);
        privacyLayout = view.findViewById(R.id.privacy_ll);

        native_3img_ad_container = view.findViewById(R.id.native_3img_ad_container);
        img_1 = view.findViewById(R.id.img_1);
        img_2 = view.findViewById(R.id.img_2);
        img_3 = view.findViewById(R.id.img_3);

        text_title = view.findViewById(R.id.text_title);
        mCTAButton = view.findViewById(R.id.btn_cta);

        if (!TextUtils.isEmpty(adData.getIconUrl())) {
            img_logo.setVisibility(View.VISIBLE);
            Glide.with(context.getApplicationContext()).load(adData.getIconUrl()).into(img_logo);

        } else {
            img_logo.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(adData.getAdLogo())) {
            ad_logo.setVisibility(View.VISIBLE);
            Glide.with(context.getApplicationContext()).load(adData.getAdLogo()).into(ad_logo);
        }

        if (!TextUtils.isEmpty(adData.getTitle())) {
            text_title.setText(adData.getTitle());
        } else {
            text_title.setText("点开有惊喜");
        }

        if (!TextUtils.isEmpty(adData.getDesc())) {
            text_desc.setText(adData.getDesc());
        } else {
            text_desc.setText("听说点开它的人都交了好运!");
        }

        // clickViews数量必须大于等于1
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(view);

        // 触发创意广告的view（点击下载 或 拨打电话 或 注册 DownloadButton的点击事件
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(mCTAButton);

        List<ImageView> imageViews = new ArrayList<>();
        int patternType = adData.getAdPatternType();

        Log.d(Constants.LOG_TAG, "patternType = " + patternType);

        if (patternType == NativeAdPatternType.NATIVE_BIG_IMAGE_AD) {

            // 双图双文、单图双文：注册mImagePoster的点击事件
            mImagePoster.setVisibility(View.VISIBLE);
            mButtonsContainer.setVisibility(View.GONE);
            native_3img_ad_container.setVisibility(View.GONE);
            mMediaViewLayout.setVisibility(View.GONE);

            clickableViews.add(mImagePoster);

            imageViews.add(mImagePoster);

        } else if (patternType == NativeAdPatternType.NATIVE_GROUP_IMAGE_AD) {
            // 三小图广告：注册native_3img_ad_container的点击事件
            native_3img_ad_container.setVisibility(View.VISIBLE);
            mImagePoster.setVisibility(View.GONE);
            mButtonsContainer.setVisibility(View.GONE);
            mMediaViewLayout.setVisibility(View.GONE);

            clickableViews.add(native_3img_ad_container);

            imageViews.add(img_1);
            imageViews.add(img_2);
            imageViews.add(img_3);
        }

        // 重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        // 作为creativeViewList传入，点击不进入详情页，直接下载或进入落地页，视频和图文广告均生效
        if (adData.getAdInteractiveType() == NativeAdInteractiveType.NATIVE_DOWNLOAD && adData.getAdAppInfo() != null) {
            privacyLayout.setVisibility(View.VISIBLE);
            adData.bindViewForInteraction(view, clickableViews, creativeViewList, img_dislike, privacyLayout, nativeAdEventListener);

        } else {
            privacyLayout.setVisibility(View.GONE);
            adData.bindViewForInteraction(view, clickableViews, creativeViewList, img_dislike, null,nativeAdEventListener);
        }

        printLogImgUrl(adData);

        //需要等到bindViewForInteraction后再去添加media
        if (!imageViews.isEmpty()) {
            adData.bindImageViews(imageViews, 0);

        } else if (patternType == NativeAdPatternType.NATIVE_VIDEO_AD) {

            int videoWidth = adData.getVideoWidth();
            int videoHeight = adData.getVideoHeight();
            Log.d(Constants.LOG_TAG, "-------------getVideoWidth----------" + videoWidth + ":" + videoHeight);

            // 视频广告，注册mMediaView的点击事件
            mImagePoster.setVisibility(View.GONE);
            native_3img_ad_container.setVisibility(View.GONE);
            mMediaViewLayout.setVisibility(View.VISIBLE);

            adData.bindMediaView(mMediaViewLayout, new NativeAdData.NativeAdMediaListener() {

                @Override
                public void onVideoLoad() {
                    Log.d(Constants.LOG_TAG, "-------------onVideoLoad--------------");
                }

                @Override
                public void onVideoError(AdError error) {
                    Log.d(Constants.LOG_TAG, "-------------onVideoError--------------:" + error.toString());
                }

                @Override
                public void onVideoStart() {
                    Log.d(Constants.LOG_TAG, "-------------onVideoStart--------------");
                }

                @Override
                public void onVideoPause() {
                    Log.d(Constants.LOG_TAG, "-------------onVideoPause--------------");
                }

                @Override
                public void onVideoResume() {
                    Log.d(Constants.LOG_TAG, "-------------onVideoResume--------------");
                }

                @Override
                public void onVideoCompleted() {
                    Log.d(Constants.LOG_TAG, "-------------onVideoCompleted--------------");
                }
            });

            mButtonsContainer.setVisibility(View.VISIBLE);

            View.OnClickListener listener = v -> {

                if (v == mPlayButton) {
                    adData.startVideo();

                } else if (v == mPauseButton) {
                    adData.pauseVideo();

                } else if (v == mStopButton) {
                    adData.stopVideo();
                }
            };

            mPlayButton.setOnClickListener(listener);

            mPauseButton.setOnClickListener(listener);

            mStopButton.setOnClickListener(listener);
        }

        View shakeView = adData.getWidgetView(80, 80);

        if (shakeView != null) {
            if (shakeView.getParent() != null) {
                ViewGroup parent = (ViewGroup) shakeView.getParent();
                parent.removeView(shakeView);
            }
            shakeLayout.addView(shakeView);
        }

        String ctaText = adData.getCTAText();
        Log.d(Constants.LOG_TAG, "ctaText:" + ctaText);

        updateCTATxt(ctaText);

        /**
         * 六要素信息展示
         */
        AdAppInfo appInfo = adData.getAdAppInfo();
        if (appInfo != null) {
            Log.d(Constants.LOG_TAG, "应用名字 = " + appInfo.getAppName());
            Log.d(Constants.LOG_TAG, "应用包名 = " + appInfo.getPackageName());
            Log.d(Constants.LOG_TAG, "应用版本 = " + appInfo.getVersionName());
            Log.d(Constants.LOG_TAG, "开发者 = " + appInfo.getDeveloper());
            Log.d(Constants.LOG_TAG, "应用品牌 = " + appInfo.getAuthorName());
            Log.d(Constants.LOG_TAG, "包大小 = " + appInfo.getAppSize());
            Log.d(Constants.LOG_TAG, "隐私条款链接 = " + appInfo.getPrivacyUrl());
            Log.d(Constants.LOG_TAG, "权限信息链接 = " + appInfo.getPermissionsUrl());

            Log.d(Constants.LOG_TAG, "\n 所有信息 = " + adData.toString());
        }

        return view;
    }

    private static void printLogImgUrl(NativeAdData adData) {
        List<GtImage> imageList = adData.getImageList();

        if (imageList != null && !imageList.isEmpty()) {
            for (int i = 0; i < imageList.size(); i++) {
                GtImage image = imageList.get(i);
                if (image != null) {
                    Log.d(Constants.LOG_TAG, "-------------imageList--------------:" + image.getWidth() + ":" + image.getHeight() + ":" + image.getImageUrl());
                }
            }

        } else {
            Log.d(Constants.LOG_TAG, "imageList is null or size is 0");
        }
    }

    public void updateCTATxt(String ctaText) {

        if (!TextUtils.isEmpty(ctaText)) {

            mCTAButton.setText(ctaText);
            mCTAButton.setVisibility(View.VISIBLE);

        } else {
            mCTAButton.setVisibility(View.INVISIBLE);
        }
    }
}