package com.adgain.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.adgain.demo.databinding.ActivityDeviceInfoBinding;
import com.adgain.demo.utils.UIUtil;
import com.adgain.sdk.AdGainSdk;

import java.util.List;

public class DeviceInfoDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDeviceInfoBinding binding = ActivityDeviceInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List.of(
                binding.oaidContainer,
                binding.sdkVersionContainer
        ).forEach(v -> v.setBackgroundColor(UIUtil.getARandomColor()));

        binding.oaid.setText(AdGainSdk.getInstance().getSdkInfo().get("oaid").toString());
        binding.sdvVersion.setText(AdGainSdk.getVersionName());
    }
}