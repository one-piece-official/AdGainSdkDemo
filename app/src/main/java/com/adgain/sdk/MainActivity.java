package com.adgain.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.adgain.demo.android.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private MainFragment mMainFragment;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentContainer = findViewById(R.id.fragment_container);
        createMainFragment();
    }
    private void createMainFragment() {
        if (mMainFragment == null) {
            Intent intent = getIntent();
            String[] logs = intent.getStringArrayExtra("logs");
            mMainFragment = MainFragment.newInstance(logs);
            getSupportFragmentManager().beginTransaction().replace(fragmentContainer.getId(), mMainFragment).commit();
        }
    }

    @Override
    public void onStateNotSaved() {
        super.onStateNotSaved();
        mMainFragment.onResume();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed() called " + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }
}