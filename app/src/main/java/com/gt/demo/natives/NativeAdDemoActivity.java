package com.gt.demo.natives;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gt.demo.databinding.DemoActivityNativeBinding;

import java.util.Map;


public class NativeAdDemoActivity extends AppCompatActivity {

    private DemoActivityNativeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DemoActivityNativeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Map.of(
                binding.nativeAdSimpleButton, NativeAdSimpleDemoActivity.class,
                binding.nativeAdListButton, NativeAdListDemoActivity.class,
                binding.nativeAdRecycleButton, NativeAdRecycleDemoActivity.class,
                binding.nativeDrawRecycleButton, NativeAdFeedDemoActivity.class
        ).entrySet().forEach(entry -> {
            entry.getKey().setOnClickListener(v -> {
                Intent intent = new Intent(NativeAdDemoActivity.this, entry.getValue());
                startActivity(intent);
            });
        });
    }

}