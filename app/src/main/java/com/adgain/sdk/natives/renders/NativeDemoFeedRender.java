package com.adgain.sdk.natives.renders;

import static com.adgain.sdk.natives.renders.NativeDemoRender.printImgUrl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.adgain.sdk.Constants;
import com.adgain.demo.android.R;
import com.adgain.sdk.utils.UIUtil;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdPatternType;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeDemoFeedRender {

    private final Context context;

    private final Map<Integer, ViewHolder> viewHolderMap = new HashMap<>();

    public NativeDemoFeedRender(Context context) {
        this.context = context;
    }

    public View renderAdView(NativeAdData adData, NativeAdEventListener nativeAdEventListener) {

        ViewHolder viewHolder = createView(context, adData);
        Log.d(Constants.LOG_TAG, "renderAdView:" + adData.getTitle());

        String iconUrl =  adData.getIconUrl();
        if (!TextUtils.isEmpty(iconUrl)) {
            Glide.with(context.getApplicationContext()).load(iconUrl).into(viewHolder.adIcon);
        }

        String title = adData.getTitle();
        viewHolder.adTitle.setText(TextUtils.isEmpty(title) ? "点击发现精彩" : title);

        String desc = adData.getDesc();
        viewHolder.adDesc.setText(TextUtils.isEmpty(desc) ? "别再犹豫，点击一下，把好运带回家！" : desc);

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(viewHolder.adTitle);
        clickableViews.add(viewHolder.adDesc);


        List<ImageView> imageViews = new ArrayList<>();

        int patternType = adData.getAdPatternType();
        Log.d(Constants.LOG_TAG, "patternType:" + patternType);

        if (patternType == NativeAdPatternType.NATIVE_BIG_IMAGE_AD) {
            viewHolder.adImage.setVisibility(View.VISIBLE);
            viewHolder.adVideoContainer.setVisibility(View.GONE);

            clickableViews.add(viewHolder.adImage);
            imageViews.add(viewHolder.adImage);
        }

        adData.bindViewForInteraction(viewHolder.rootView, clickableViews, nativeAdEventListener);

        printImgUrl(adData);

        // 要等到 bindViewForInteraction调用完，再去添加 video
        if (!imageViews.isEmpty()) {
            Glide.with(context).load(adData.getImageList().get(0).imageUrl).into(viewHolder.adImage);

        } else if (patternType == NativeAdPatternType.NATIVE_VIDEO_AD) {

            // 视频广告，注册mMediaView的点击事件
            viewHolder.adImage.setVisibility(View.GONE);
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
        }

        View shakeView = adData.getWidgetView(80, 80);
        if (shakeView != null) {
            UIUtil.removeViewFromParent(shakeView);
            viewHolder.shakeLayout.addView(shakeView);
        }

        String ctaText = adData.getCTAText();
        Log.d(Constants.LOG_TAG, "ctaText:" + ctaText);

        if (!TextUtils.isEmpty(ctaText)) {
            viewHolder.adCta.setText(ctaText);
            viewHolder.adCta.setVisibility(View.VISIBLE);
        } else {
            viewHolder.adCta.setVisibility(View.INVISIBLE);
        }

        return viewHolder.rootView;
    }

    private ViewHolder createView(Context context, NativeAdData adData) {
        Log.d(Constants.LOG_TAG, "---------createView----------" + adData.hashCode());

        ViewHolder viewHolder = viewHolderMap.get(adData.hashCode());

        if (viewHolder == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.demo_native_feed_ad_item, null);
            viewHolder = new ViewHolder(view);
            viewHolderMap.put(adData.hashCode(), viewHolder);
        }

        UIUtil.removeViewFromParent(viewHolder.rootView);

        return viewHolder;
    }

    private static class ViewHolder {
        View rootView;
        ImageView adIcon;
        TextView adTitle;
        TextView adDesc;
        ImageView adImage;
        FrameLayout adVideoContainer;
        FrameLayout shakeLayout;
        Button adCta;

        ViewHolder(View view) {
            rootView = view;
            adIcon = view.findViewById(R.id.ad_icon);
            adTitle = view.findViewById(R.id.ad_title);
            adDesc = view.findViewById(R.id.ad_desc);
            adImage = view.findViewById(R.id.ad_image);
            adVideoContainer = view.findViewById(R.id.ad_video_container);
            shakeLayout = view.findViewById(R.id.shake_layout);
            adCta = view.findViewById(R.id.ad_cta);
        }
    }
}