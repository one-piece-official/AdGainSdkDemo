package com.adgain.demo.wxapi;

import android.content.Context;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 微信SDK辅助类，用于管理微信API的初始化和调用小程序功能
 */
public class WeChatHelper {
    private static final String TAG = "WeChatHelper";
    private static WeChatHelper instance;
    
    // 微信开放平台应用AppID
    private static String APP_ID = "your_wechat_app_id"; // 需要替换为您的微信AppID
    
    private IWXAPI api;
    private Context mContext;
    
    private WeChatHelper(Context context) {
        this.mContext = context.getApplicationContext();
        // 初始化IWXAPI实例
        api = WXAPIFactory.createWXAPI(mContext, APP_ID, false);
        // 注册到微信
        api.registerApp(APP_ID);
    }
    
    /**
     * 获取单例实例
     */
    public static WeChatHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (WeChatHelper.class) {
                if (instance == null) {
                    instance = new WeChatHelper(context);
                }
            }
        }
        return instance;
    }
    
    /**
     * 检查微信是否已安装
     */
    public boolean isWXAppInstalled() {
        return api != null && api.isWXAppInstalled();
    }
    
    /**
     * 拉起微信小程序
     * @param userName 小程序原始ID（"gh_"开头的字符串）
     * @param path 小程序页面路径
     * @param miniprogramType 小程序类型 0正式版，1测试版，2预览版
     * @return 是否成功发起请求
     */
    public boolean launchMiniProgram(String userName, String path, int miniprogramType) {
        if (!isWXAppInstalled()) {
            Toast.makeText(mContext, "请先安装微信客户端", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        // 小程序原始ID
        req.userName = userName;
        // 小程序页面路径
        if (path != null && !path.isEmpty()) {
            req.path = path;
        }
        // 小程序类型
        // WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE = 0 正式版
        // WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST = 1 测试版
        // WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW = 2 预览版
        req.miniprogramType = miniprogramType;
        
        return api.sendReq(req);
    }
    
    /**
     * 更新AppID
     */
    public void updateAppId(String appId) {
        if (appId != null && !appId.isEmpty()) {
            APP_ID = appId;
            // 重新注册AppID
            api.registerApp(APP_ID);
        }
    }
    
    /**
     * 获取IWXAPI实例
     */
    public IWXAPI getWxApi() {
        return api;
    }
} 