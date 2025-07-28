package com.adgain.sdk.utils;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adgain.demo.android.R;
import com.adgain.sdk.ui.MainActivity;

public class DialogAct extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_permission);
        SharedPreferences sharedPreferences = getSharedPreferences("permission", MODE_PRIVATE);
        boolean isFirst = sharedPreferences.getBoolean("isFirst", true);
        if (!isFirst) {
            Intent intent = new Intent(DialogAct.this, MainActivity.class);
            startActivity(intent);
        }
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.disagreeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.agreeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("permission", MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("isFirst", false).apply();
                Intent intent = new Intent(DialogAct.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        TextView content = findViewById(R.id.content);
        SpannableStringBuilder builder = new SpannableStringBuilder(content.getText().toString());
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gdsmilemg.datads.cn/privacy-policy.html"));
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.BLUE); // 设置超链接颜色
                ds.setUnderlineText(true); // 去除下划线
            }
        }, 25, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setText(builder);
        content.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
