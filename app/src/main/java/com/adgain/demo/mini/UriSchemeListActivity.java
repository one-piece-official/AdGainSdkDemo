package com.adgain.demo.mini;

import android.app.Activity;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.adgain.demo.R;

public class UriSchemeListActivity extends Activity {
    private TextView app_name;
    private Button clear;
    private ImageView icon;
    private Button paste;
    private EditText uriScheme;
    private TextView uriSchemeLabel;
    private RelativeLayout uri_scheme_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_weixin_deeplink);
        this.uriScheme = (EditText) findViewById(R.id.uri_scheme);
        this.clear = (Button) findViewById(R.id.clear);
        this.paste = (Button) findViewById(R.id.paste);
        this.uri_scheme_item = (RelativeLayout) findViewById(R.id.uri_scheme_item);
        this.icon = (ImageView) findViewById(R.id.icon);
        this.app_name = (TextView) findViewById(R.id.app_name);
        this.uriSchemeLabel = (TextView) findViewById(R.id.itemtext);
        this.uri_scheme_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppInfo appInfo = UriSchemeListActivity.this.loadData();
                if (appInfo != null && appInfo.isInstalled()) {
                    APPUtils.openAppWithUriScheme(UriSchemeListActivity.this, appInfo.getUriScheme(), null, true);
                }
            }
        });
        this.paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cbm = (ClipboardManager) UriSchemeListActivity.this.getSystemService("clipboard");
                if (cbm != null && cbm.hasPrimaryClip()) {
                    if (cbm.getPrimaryClip().getItemCount() > 0) {
                        UriSchemeListActivity.this.uriScheme.setText("");
                        UriSchemeListActivity.this.uriScheme.setText(cbm.getPrimaryClip().getItemAt(0).getText());
                        return;
                    }
                    Toast.makeText(UriSchemeListActivity.this, "剪切板中无内容！", 0).show();
                }
            }
        });
        this.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UriSchemeListActivity.this.uriScheme.setText("");
                UriSchemeListActivity.this.loadData();
            }
        });
        this.uriScheme.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                UriSchemeListActivity.this.loadData();
            }
        });
    }

    public AppInfo loadData() {
        if (TextUtils.isEmpty(this.uriScheme.getText().toString())) {
            return null;
        }
        String uri_scheme = this.uriScheme.getText().toString();
        AppInfo appInfo = APPUtils.getAppInfoByScheme(this, uri_scheme);
        if (appInfo == null) {
            appInfo = new AppInfo();
            appInfo.setUriScheme(uri_scheme);
            appInfo.setInstalled(false);
            appInfo.setAppName("未知");
            appInfo.setAppIconDrawable(ContextCompat.getDrawable(this, R.drawable.background_green));
        }
        this.icon.setImageDrawable(appInfo.getAppIconDrawable());
        this.app_name.setText(appInfo.getAppName());
        this.uriSchemeLabel.setText(appInfo.getUriScheme());
        return appInfo;
    }
}