package com.adgain.sdk.natives.renders;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adgain.sdk.Constants;
import com.adgain.demo.android.R;
import com.adgain.sdk.utils.UIUtil;
import com.adgain.sdk.api.AdAppInfo;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdGainImage;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdPatternType;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 原生广告（自渲染）渲染View的示例
 */
public class NativeDemoRender {

    private Context context;

    private final Map<Integer, ViewHolder> viewHolderMap = new HashMap<>();

    public NativeDemoRender(Context activity) {
        this.context = activity;
    }

    public View renderAdView(NativeAdData adData, NativeAdEventListener nativeAdEventListener) {

        ViewHolder viewHolder = createView(context, adData);
        Log.d(Constants.LOG_TAG, "renderAdView:" + adData.getTitle() +" " + adData.getIconUrl() );

        if (!TextUtils.isEmpty(adData.getIconUrl())) {
            viewHolder.adIcon.setVisibility(View.VISIBLE);
            Glide.with(context.getApplicationContext()).load(adData.getIconUrl()).into(viewHolder.adIcon);
        } else {
            viewHolder.adIcon.setVisibility(View.GONE);
        }

        String title = adData.getTitle();
        viewHolder.adTitle.setText(TextUtils.isEmpty(title) ? "点击发现精彩" : title);

        String desc = adData.getDesc();
        viewHolder.adDesc.setText(TextUtils.isEmpty(desc) ? "别再犹豫，点击一下，把好运带回家！" : desc);

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(viewHolder.rootView);
        clickableViews.add(viewHolder.adCta);

        List<ImageView> imageViews = new ArrayList<>();

        int patternType = adData.getAdPatternType();
        Log.d(Constants.LOG_TAG, "patternType = " + patternType);

        viewHolder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup) viewHolder.adVG.getParent()).removeView(viewHolder.adVG);
            }
        });

        if (patternType == NativeAdPatternType.NATIVE_BIG_IMAGE_AD) {
            // 原生大图广告：注册mImagePoster的点击事件
            viewHolder.adImage.setVisibility(View.VISIBLE);
            viewHolder.videoControlContainer.setVisibility(View.GONE);
            viewHolder.adImageGroupContainer.setVisibility(View.GONE);
            viewHolder.adVideoContainer.setVisibility(View.GONE);

            clickableViews.add(viewHolder.adImage);

            imageViews.add(viewHolder.adImage);

        } else if (patternType == NativeAdPatternType.NATIVE_GROUP_IMAGE_AD) {
            // 三小图广告：注册3小图视图的点击事件
            viewHolder.adImageGroupContainer.setVisibility(View.VISIBLE);
            viewHolder.adImage.setVisibility(View.GONE);
            viewHolder.videoControlContainer.setVisibility(View.GONE);
            viewHolder.adVideoContainer.setVisibility(View.GONE);

            clickableViews.add(viewHolder.adImageGroupContainer);

            imageViews.add(viewHolder.adImage1);
            imageViews.add(viewHolder.adImage2);
            imageViews.add(viewHolder.adImage3);
        }

        adData.bindViewForInteraction(viewHolder.rootView, clickableViews, nativeAdEventListener);


        printImgUrl(adData);

        // bindViewForInteraction 调用后再添加 media
        if (patternType == NativeAdPatternType.NATIVE_VIDEO_AD) {
            int videoWidth = adData.getVideoWidth();
            int videoHeight = adData.getVideoHeight();
            Log.d(Constants.LOG_TAG, "-------------getVideoSize----------" + videoWidth + ":" + videoHeight);

            // 视频广告，注册 mediaView 的点击事件
            viewHolder.adImage.setVisibility(View.GONE);
            viewHolder.adImageGroupContainer.setVisibility(View.GONE);
            viewHolder.adVideoContainer.setVisibility(View.VISIBLE);

            adData.bindMediaView(viewHolder.adVideoContainer, new NativeAdData.NativeAdMediaListener() {

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

            // 播放控制按钮
            viewHolder.videoControlContainer.setVisibility(View.VISIBLE);
            viewHolder.playVideo.setOnClickListener(v -> adData.startVideo());
            viewHolder.pauseVideo.setOnClickListener(v -> adData.pauseVideo());
            viewHolder.stopVideo.setOnClickListener(v -> adData.stopVideo());

        } else {
            Glide.with(context).load(adData.getImageList().get(0).imageUrl).into(viewHolder.adImage);
        }

        String ctaText = adData.getCTAText();
        Log.d(Constants.LOG_TAG, "ctaText:" + ctaText);
        if (TextUtils.isEmpty(ctaText)) {
            viewHolder.adCta.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.adCta.setText(ctaText);
            viewHolder.adCta.setVisibility(View.VISIBLE);
        }

        // 六要素信息展示
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

            Log.d(Constants.LOG_TAG, "\n 所有信息 = " + adData);
        }

        return viewHolder.rootView;
    }

    public static void printImgUrl(NativeAdData adData) {
        List<AdGainImage> imageList = adData.getImageList();
        if (imageList != null && !imageList.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageList.stream().filter(Objects::nonNull).forEach(image ->
                        Log.d(Constants.LOG_TAG, "-------------imageList--------------:" + image.getWidth() + ":" + image.getHeight() + ":" + image.getImageUrl())
                );
            }
        } else {
            Log.d(Constants.LOG_TAG, "imageList is null or size is 0");
        }
    }

    private ViewHolder createView(Context context, NativeAdData adData) {

        this.context = context;

        ViewHolder viewHolder = viewHolderMap.get(adData.hashCode());

        if (viewHolder == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.demo_native_ad_item_normal, null);
            viewHolder = new ViewHolder(view);
            viewHolderMap.put(adData.hashCode(), viewHolder);
        }

        UIUtil.removeViewFromParent(viewHolder.rootView);
        return viewHolder;
    }

    /**
     * ViewHolder 类来管理所有视图组件
     */
    private static class ViewHolder {
        View rootView;
        FrameLayout adVG;
        ImageView close;
        ImageView adIcon;
        TextView adTitle;
        TextView adDesc;
        LinearLayout videoControlContainer;
        ImageButton playVideo;
        ImageButton pauseVideo;
        ImageButton stopVideo;
        FrameLayout adVideoContainer;
        ImageView adImage;
        LinearLayout adImageGroupContainer;
        ImageView adImage1;
        ImageView adImage2;
        ImageView adImage3;
        FrameLayout shakeLayout;
        Button adCta;

        ViewHolder(View view) {
            rootView = view;
            adVG = view.findViewById(R.id.adVG);
            close = view.findViewById(R.id.close);
            adIcon = view.findViewById(R.id.ad_icon);
            adTitle = view.findViewById(R.id.ad_title);
            adDesc = view.findViewById(R.id.ad_desc);
            videoControlContainer = view.findViewById(R.id.video_control_container);
            playVideo = view.findViewById(R.id.play_video);
            pauseVideo = view.findViewById(R.id.pause_video);
            stopVideo = view.findViewById(R.id.stop_video);
            adVideoContainer = view.findViewById(R.id.ad_video_container);
            adImage = view.findViewById(R.id.ad_image);
            adImageGroupContainer = view.findViewById(R.id.ad_image_group_container);
            adImage1 = view.findViewById(R.id.ad_image1);
            adImage2 = view.findViewById(R.id.ad_image2);
            adImage3 = view.findViewById(R.id.ad_image3);
            shakeLayout = view.findViewById(R.id.shake_layout);
            adCta = view.findViewById(R.id.ad_cta);
        }
    }
}