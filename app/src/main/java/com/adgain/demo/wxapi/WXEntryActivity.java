package com.adgain.demo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * 微信回调Activity
 * 必须放在packageName.wxapi包下，并命名为WXEntryActivity
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 获取微信API实例
        api = WeChatHelper.getInstance(this).getWxApi();
        
        try {
            // 处理微信回调
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
            Log.e(TAG, "微信回调处理异常: " + e.getMessage());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        
        // 处理微信回调
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        // 微信发送请求到第三方应用
        Log.d(TAG, "微信请求: " + baseReq.getType());
        finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        // 第三方应用响应微信的请求
        Log.d(TAG, "微信响应: type=" + baseResp.getType() + ", errCode=" + baseResp.errCode);
        
        // 处理不同类型的响应
        if (baseResp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            // 小程序回调
            WXLaunchMiniProgram.Resp miniProgramResp = (WXLaunchMiniProgram.Resp) baseResp;
            String extraData = miniProgramResp.extMsg; // 小程序返回的数据
            Log.d(TAG, "小程序返回数据: " + extraData);
            
            // 处理小程序返回的结果
            // 错误码 0=成功, -1=错误, -2=用户取消
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    // 成功
                    Log.d(TAG, "小程序调用成功");
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    // 用户取消
                    Log.d(TAG, "用户取消小程序调用");
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    // 认证被拒绝
                    Log.d(TAG, "小程序认证被拒绝");
                    break;
                default:
                    // 其他错误
                    Log.d(TAG, "小程序调用错误: " + baseResp.errCode);
                    break;
            }
        }
        
        // 关闭Activity
        finish();
    }
} 