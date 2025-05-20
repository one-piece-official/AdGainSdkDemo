package com.adgain.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.adgain.demo.wxapi.WeChatHelper;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;

/**
 * 微信小程序示例Activity
 */
public class WeChatMiniProgramDemoActivity extends AppCompatActivity {

    private EditText etAppId;
    private EditText etMiniProgramId;
    private EditText etPath;
    private RadioGroup rgMiniProgramType;
    private Button btnLaunchMiniProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_miniprogram_demo);

        // 初始化视图
        initViews();
        // 设置点击事件
        setupClickListeners();
    }

    private void initViews() {
        etAppId = findViewById(R.id.et_app_id);
        etMiniProgramId = findViewById(R.id.et_miniprogram_id);
        etPath = findViewById(R.id.et_miniprogram_path);
        rgMiniProgramType = findViewById(R.id.rg_miniprogram_type);
        btnLaunchMiniProgram = findViewById(R.id.btn_launch_miniprogram);
    }

    private void setupClickListeners() {
        // 启动小程序按钮点击事件
        btnLaunchMiniProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMiniProgram();
            }
        });
    }

    /**
     * 启动微信小程序
     */
    private void launchMiniProgram() {
        // 获取微信AppID
        String appId = etAppId.getText().toString().trim();
        if (!appId.isEmpty()) {
            // 更新AppID
            WeChatHelper.getInstance(this).updateAppId(appId);
        }

        // 获取小程序原始ID
        String miniProgramId = etMiniProgramId.getText().toString().trim();
        if (miniProgramId.isEmpty()) {
            Toast.makeText(this, "请输入小程序原始ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取小程序页面路径
        String path = etPath.getText().toString().trim();

        // 获取小程序类型
        int miniProgramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE; // 默认正式版
        int checkedId = rgMiniProgramType.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_miniprogram_test) {
            miniProgramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST; // 测试版
        } else if (checkedId == R.id.rb_miniprogram_preview) {
            miniProgramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW; // 预览版
        }

        // 检查微信是否已安装
        if (!WeChatHelper.getInstance(this).isWXAppInstalled()) {
            Toast.makeText(this, "请先安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }

        // 启动微信小程序
        boolean result = WeChatHelper.getInstance(this).launchMiniProgram(miniProgramId, path, miniProgramType);
        if (!result) {
            Toast.makeText(this, "启动小程序失败", Toast.LENGTH_SHORT).show();
        }
    }
} 