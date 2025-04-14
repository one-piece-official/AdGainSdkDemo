package com.gt.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class LeakActivity extends AppCompatActivity {
    // 静态 Handler 会导致内存泄漏
    private static Handler sHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            // 这里引用外部 Activity 的 TextView，会导致内存泄漏
            if (msg.what == 1 && sTextView != null) {
                sTextView.setText("Handler message received!");
            }
        }
    };

    // 静态 TextView 引用
    private static TextView sTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak);

        sTextView = findViewById(R.id.textView);
        
        // 发送延迟消息
        sHandler.sendEmptyMessageDelayed(1, 10000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 故意不移除消息，造成内存泄漏
        // sHandler.removeCallbacksAndMessages(null);
    }
} 