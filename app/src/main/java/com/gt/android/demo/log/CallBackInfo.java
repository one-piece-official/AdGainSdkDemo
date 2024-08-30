package com.gt.android.demo.log;


public class CallBackInfo {

    public static String[] INTERSTITIAL_CALLBACK = {
            "onInterstitialAdLoadSuccess",
            "onInterstitialAdCacheSuccess",
            "onInterstitialAdPlay",
            "onInterstitialAdPLayEnd",
            "onInterstitialAdClick",
            "onInterstitialAdSkip",
            "onInterstitialAdClosed",
            "onInterstitialAdLoadError",
            "onInterstitialAdShowError"};

    public static String[] SPLASH_CALLBACK = {
            "onSplashAdLoadSuccess",
            "onSplashAdLoadFail",
            "onSplashAdShow",
            "onSplashAdShowError",
            "onSplashAdClick",
            "onSplashAdClose"};

    public static String[] NATIVE_CALLBACK = {
            "onAdLoad",
            "onAdError",
            "onAdExposed",
            "onAdClicked",
            "onAdRenderFail"};

}
