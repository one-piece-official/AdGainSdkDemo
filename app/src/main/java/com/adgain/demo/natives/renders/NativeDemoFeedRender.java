package com.adgain.demo.natives.renders;

import static com.adgain.demo.natives.renders.NativeDemoRender.printImgUrl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.adgain.demo.Constants;
import com.adgain.demo.databinding.DemoNativeFeedAdItemBinding;
import com.adgain.demo.utils.UIUtil;
import com.adgain.sdk.api.AdError;
import com.adgain.sdk.api.NativeAdData;
import com.adgain.sdk.api.NativeAdEventListener;
import com.adgain.sdk.api.NativeAdInteractiveType;
import com.adgain.sdk.api.NativeAdPatternType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeDemoFeedRender {

    private final Context context;

    private final Map<Integer, DemoNativeFeedAdItemBinding> viewBindingMap = new HashMap<>();

    public NativeDemoFeedRender(Context context) {
        this.context = context;
    }

    public View renderAdView(NativeAdData adData, NativeAdEventListener nativeAdEventListener) {

        DemoNativeFeedAdItemBinding binding = createView(context, adData);
        Log.d(Constants.LOG_TAG, "renderAdView:" + adData.getTitle());

        List.of(adData.getAdLogo(), adData.getIconUrl()).stream().filter(url -> !TextUtils.isEmpty(url))
                .findFirst().ifPresent(url -> Glide.with(context.getApplicationContext()).load(url).into(binding.adLogo));

        String title = adData.getTitle();
        binding.adTitle.setText(TextUtils.isEmpty(title) ? "点击发现精彩" : title);
        String desc = adData.getDesc();
        binding.adDesc.setText(TextUtils.isEmpty(desc) ? "别再犹豫，点击一下，把好运带回家！" : desc);

        //clickViews数量必须大于等于1
        List<View> clickableViews = new ArrayList<>();
        //可以被点击的view, 也可以把convertView放进来意味item可被点击
        clickableViews.add(binding.adTitle);
        clickableViews.add(binding.adDesc);

        // 触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        // 所有广告类型，注册 cta 的点击事件
        creativeViewList.add(binding.adCta);

        List<ImageView> imageViews = new ArrayList<>();
        int patternType = adData.getAdPatternType();
        Log.d(Constants.LOG_TAG, "patternType:" + patternType);

        if (patternType == NativeAdPatternType.NATIVE_BIG_IMAGE_AD) {
            // 大图 广告
            binding.adImage.setVisibility(View.VISIBLE);
            binding.adVideoContainer.setVisibility(View.GONE);
            clickableViews.add(binding.adImage);
            imageViews.add(binding.adImage);
        }

        //重要! 这个涉及到广告计费，必须正确调用。
        // 作为 creativeViewList 传入，点击不进入详情页，直接下载或进入落地页，视频和图文广告均生效
        if (adData.getAdInteractiveType() == NativeAdInteractiveType.NATIVE_DOWNLOAD && adData.getAdAppInfo() != null) {
            adData.bindViewForInteraction(binding.getRoot(), clickableViews,  nativeAdEventListener);

        } else {
            adData.bindViewForInteraction(binding.getRoot(), clickableViews, nativeAdEventListener);
        }

        printImgUrl(adData);

        // 要等到 bindViewForInteraction调用完，再去添加 video
        if (!imageViews.isEmpty()) {
//            adData.bindImageViews(imageViews, 0);

        } else if (patternType == NativeAdPatternType.NATIVE_VIDEO_AD) {

            // 视频广告，注册mMediaView的点击事件
            binding.adImage.setVisibility(View.GONE);
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
        }

        View shakeView = adData.getWidgetView(80, 80);
        if (shakeView != null) {
            UIUtil.removeViewFromParent(shakeView);
            binding.shakeLayout.addView(shakeView);
        }
        /**
         * 营销组件
         * 支持项目：智能电话（点击跳转拨号盘），外显表单
         *  creativeViewList 绑定营销组件监听视图，注意：creativeViewList 中的视图不可调用setOnClickListener，否则SDK功能可能受到影响
         *  ad.getCTAText 判断拉取广告是否包含营销组件，如果包含组件，展示组件按钮，否则展示
         */
        String ctaText = adData.getCTAText(); //获取组件文案
        Log.d(Constants.LOG_TAG, "ctaText:" + ctaText);

        if (!TextUtils.isEmpty(ctaText)) {
            binding.adCta.setText(ctaText);
            binding.adCta.setVisibility(View.VISIBLE);
        } else {
            binding.adCta.setVisibility(View.INVISIBLE);
        }

        return binding.getRoot();
    }

    private DemoNativeFeedAdItemBinding createView(Context context, NativeAdData adData) {
        Log.d(Constants.LOG_TAG, "---------createView----------" + adData.hashCode());

        DemoNativeFeedAdItemBinding viewBinding = viewBindingMap.get(adData.hashCode());

        if (viewBinding == null) {
            viewBinding = DemoNativeFeedAdItemBinding.inflate(LayoutInflater.from(context));
            viewBindingMap.put(adData.hashCode(), viewBinding);
        }

        UIUtil.removeViewFromParent(viewBinding.getRoot());

        return viewBinding;
    }
}