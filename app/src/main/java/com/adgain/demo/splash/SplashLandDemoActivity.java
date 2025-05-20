package com.adgain.demo.splash;

import android.view.View;
import android.view.ViewGroup;

import com.adgain.demo.databinding.DemoActivitySplashLandBinding;
import com.adgain.demo.utils.UIUtil;

/**
 * Author :
 * Date   :   2025/4/16
 * Time   :   17:03
 */
public class SplashLandDemoActivity extends SplashDemoActivity {
    private DemoActivitySplashLandBinding binding;
    protected View getView() {
        binding = DemoActivitySplashLandBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected ViewGroup getSplashContainer() {
        return binding.splashContainer;
    }

    protected int getSplashHeight() {
        return UIUtil.getScreenHeightInPx(this);
    }
}
