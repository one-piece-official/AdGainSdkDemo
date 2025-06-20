package com.adgain.sdk.natives;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.adgain.demo.android.R;


public class NativeAdDemoActivity extends AppCompatActivity {

    private Button nativeAdSimpleButton;
    private Button nativeAdListButton;
    private Button nativeAdRecycleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity_native);

        nativeAdSimpleButton = findViewById(R.id.native_ad_simple_button);
        nativeAdListButton = findViewById(R.id.native_ad_list_button);
        nativeAdRecycleButton = findViewById(R.id.native_ad_recycle_button);

        nativeAdSimpleButton.setOnClickListener(v -> {
            Intent intent = new Intent(NativeAdDemoActivity.this, NativeAdSimpleDemoActivity.class);
            startActivity(intent);
        });

        nativeAdListButton.setOnClickListener(v -> {
            Intent intent = new Intent(NativeAdDemoActivity.this, NativeAdListDemoActivity.class);
            startActivity(intent);
        });

        nativeAdRecycleButton.setOnClickListener(v -> {
            Intent intent = new Intent(NativeAdDemoActivity.this, NativeAdRecycleDemoActivity.class);
            startActivity(intent);
        });

    }

}