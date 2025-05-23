package com.adgain.demo.natives.renders;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.adgain.demo.Constants;
import com.adgain.demo.databinding.DemoNativeAdItemNormalBinding;
import com.adgain.demo.utils.UIUtil;
import com.adgain.sdk.api.AdAppInfo;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.AdGainImage;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdInteractiveType;
import com.adgain.sdk.api.NativeAdPatternType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 原生广告（自渲染）渲染View的示例
 */
public class NativeDemoRender {

    public static void printImgUrl(NativeAdData adData) {
        List<AdGainImage> imageList = adData.getImageList();
        if (imageList != null && !imageList.isEmpty()) {
            imageList.stream().filter(Objects::nonNull).forEach(image ->
                    Log.d(Constants.LOG_TAG, "-------------imageList--------------:" + image.getWidth() + ":" + image.getHeight() + ":" + image.getImageUrl())
            );
        } else {
            Log.d(Constants.LOG_TAG, "imageList is null or size is 0");
        }
    }

    private Context context;

    private final Map<Integer, DemoNativeAdItemNormalBinding> viewBindingMap = new HashMap<>();

    public NativeDemoRender(Context activity) {
        this.context = activity;
    }

    public View renderAdView(NativeAdData adData, NativeAdEventListener nativeAdEventListener) {

        DemoNativeAdItemNormalBinding binding = createView(context, adData);
        Log.d(Constants.LOG_TAG, "renderAdView:" + adData.getTitle());

        if (!TextUtils.isEmpty(adData.getIconUrl())) {
            binding.adLogo.setVisibility(View.VISIBLE);
            Glide.with(context.getApplicationContext()).load(adData.getIconUrl()).into(binding.adLogo);
        } else {
            binding.adLogo.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(adData.getAdLogo())) {
            binding.adChannelLogo.setVisibility(View.VISIBLE);
            Glide.with(context.getApplicationContext()).load(adData.getAdLogo()).into(binding.adChannelLogo);
        }
        String title = adData.getTitle();
        binding.adTitle.setText(TextUtils.isEmpty(title) ? "点击发现精彩" : title);
        String desc = adData.getDesc();
        binding.adDesc.setText(TextUtils.isEmpty(desc) ? "别再犹豫，点击一下，把好运带回家！" : desc);

        // clickViews数量必须大于等于1
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(binding.getRoot());
        clickableViews.add(binding.adCta);
        // 触发创意广告的view（点击下载 或 拨打电话 或 注册 DownloadButton的点击事件
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(binding.adCta);

        List<ImageView> imageViews = new ArrayList<>();
        int patternType = adData.getAdPatternType();
        Log.d(Constants.LOG_TAG, "patternType = " + patternType);
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup) binding.adVG.getParent()).removeView(binding.adVG);
            }
        });
        if (patternType == NativeAdPatternType.NATIVE_BIG_IMAGE_AD) {
            // 原生大图广告：注册mImagePoster的点击事件
            binding.adImage.setVisibility(View.VISIBLE);
            binding.videoControlContainer.setVisibility(View.GONE);
            binding.adImageGroupContainer.setVisibility(View.GONE);
            binding.adVideoContainer.setVisibility(View.GONE);

            clickableViews.add(binding.adImage);

            imageViews.add(binding.adImage);

        } else if (patternType == NativeAdPatternType.NATIVE_GROUP_IMAGE_AD) {
            // 三小图广告：注册3小图视图的点击事件
            binding.adImageGroupContainer.setVisibility(View.VISIBLE);
            binding.adImage.setVisibility(View.GONE);
            binding.videoControlContainer.setVisibility(View.GONE);
            binding.adVideoContainer.setVisibility(View.GONE);

            clickableViews.add(binding.adImageGroupContainer);

            imageViews.add(binding.adImage1);
            imageViews.add(binding.adImage2);
            imageViews.add(binding.adImage3);
        }

        // 重要! 这个涉及到广告计费，必须正确调用。
        // 作为 creativeViewList 传入，点击不进入详情页，直接下载或进入落地页，视频和图文广告均生效
        if (adData.getAdInteractiveType() == NativeAdInteractiveType.NATIVE_DOWNLOAD && adData.getAdAppInfo() != null) {
            adData.bindViewForInteraction(binding.getRoot(), clickableViews, nativeAdEventListener);

        } else {
            adData.bindViewForInteraction(binding.getRoot(), clickableViews, nativeAdEventListener);
        }


        printImgUrl(adData);

        // bindViewForInteraction 调用后再添加 media
        if (patternType == NativeAdPatternType.NATIVE_VIDEO_AD) {
            int videoWidth = adData.getVideoWidth();
            int videoHeight = adData.getVideoHeight();
            Log.d(Constants.LOG_TAG, "-------------getVideoSize----------" + videoWidth + ":" + videoHeight);

            // 视频广告，注册 mediaView 的点击事件
            binding.adImage.setVisibility(View.GONE);
            binding.adImageGroupContainer.setVisibility(View.GONE);
            binding.adVideoContainer.setVisibility(View.VISIBLE);

            adData.bindMediaView(binding.adVideoContainer, new NativeAdData.NativeAdMediaListener() {

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
            binding.videoControlContainer.setVisibility(View.VISIBLE);
            binding.playVideo.setOnClickListener(v -> adData.startVideo());
            binding.pauseVideo.setOnClickListener(v -> adData.pauseVideo());
            binding.stopVideo.setOnClickListener(v -> adData.stopVideo());
        } else {
            if (adData.getImageList().size() > 0)
                Glide.with(context).load(adData.getImageList().get(0).imageUrl).into(binding.adImage);
        }

        View shakeView = adData.getWidgetView(80, 80);

        if (shakeView != null) {
            UIUtil.removeViewFromParent(shakeView);
            binding.shakeLayout.addView(shakeView);
        }

        String ctaText = adData.getCTAText();
        Log.d(Constants.LOG_TAG, "ctaText:" + ctaText);
        if (TextUtils.isEmpty(ctaText)) {
            binding.adCta.setVisibility(View.INVISIBLE);
        } else {
            binding.adCta.setText(ctaText);
            binding.adCta.setVisibility(View.VISIBLE);
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

        return binding.getRoot();
    }

    private DemoNativeAdItemNormalBinding createView(Context context, NativeAdData adData) {

        this.context = context;

        DemoNativeAdItemNormalBinding binding = viewBindingMap.get(adData.hashCode());

        if (binding == null) {
            binding = DemoNativeAdItemNormalBinding.inflate(LayoutInflater.from(context));
            viewBindingMap.put(adData.hashCode(), binding);
        }

        UIUtil.removeViewFromParent(binding.getRoot());
        return binding;
    }
}