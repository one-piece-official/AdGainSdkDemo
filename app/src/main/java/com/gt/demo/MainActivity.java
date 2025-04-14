package com.gt.demo;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private MainFragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(Constants.LOG_TAG, "------------startActivity--------onCreate-------" + System.currentTimeMillis());

        createMainFragment();

        requestPermission(this);

        // 添加启动 LeakActivity 的按钮
        findViewById(R.id.start_leak_activity).setOnClickListener(v -> {
            startActivity(new Intent(this, LeakActivity.class));
        });

//        testJavaCrash();
    }

    public static void requestPermission(Activity activity) {
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                boolean read_phone_state = isPermissionGranted(activity, READ_PHONE_STATE);
                boolean write_external_storage = isPermissionGranted(activity, WRITE_EXTERNAL_STORAGE);
                boolean access_fine_location = isPermissionGranted(activity, ACCESS_FINE_LOCATION);

                if (!read_phone_state || !write_external_storage || !access_fine_location) {
                    activity.requestPermissions(new String[]{READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION}, PERMISSION_GRANTED);
                }
            }
        }
    }

    public static boolean isPermissionGranted(final Context context, final String permission) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context != null) {
                return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void testJavaCrash() {
        throw new SecurityException("Sdk test Crash from lance!");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mMainFragment != null && intent != null) {
            String[] logs = intent.getStringArrayExtra("logs");
            mMainFragment.setLogs(logs);
        }
    }

    private void createMainFragment() {

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null && mMainFragment == null) {

            mMainFragment = new MainFragment();
            Intent intent = getIntent();
            String[] logs = intent.getStringArrayExtra("logs");

            mMainFragment.setLogs(logs);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, mMainFragment).commit();

        }
    }

    @Override
    public void onStateNotSaved() {
        super.onStateNotSaved();
        mMainFragment.onResume();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed() called " + getFragmentManager().getBackStackEntryCount());
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}