package com.adgain.demo.mini;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class APPUtils {
    public static boolean isPkgInstalled(Context context, String pkgName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public static AppInfo getAppInfoByScheme(Context context, String uri_scheme) {
        AppInfo appInfo = null;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(uri_scheme));
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfo != null) {
                AppInfo appInfo2 = new AppInfo();
                try {
                    appInfo2.setAppIconDrawable(resolveInfo.loadIcon(context.getPackageManager()));
                    appInfo2.setPackageName(resolveInfo.activityInfo.packageName);
                    appInfo2.setAppName((String) resolveInfo.loadLabel(context.getPackageManager()));
                    appInfo2.setAppIcon(resolveInfo.getIconResource());
                    appInfo2.setUriScheme(uri_scheme);
                    appInfo2.setInstalled(true);
                    appInfo = appInfo2;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return appInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AppInfo getPChklstInfoByPkgName(Context context, String pkgName) {
        Intent intent = new Intent();
        intent.setPackage(pkgName);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo == null) {
            return null;
        }
        AppInfo appInfo = new AppInfo();
        appInfo.setAppIconDrawable(resolveInfo.loadIcon(context.getPackageManager()));
        appInfo.setAppName((String) resolveInfo.loadLabel(context.getPackageManager()));
        appInfo.setAppIcon(resolveInfo.getIconResource());
        appInfo.setInstalled(true);
        return appInfo;
    }

    public static void openAppWithUriScheme(Context context, String uri_scheme, String packageName) {
        openAppWithUriScheme(context, uri_scheme, packageName, true);
    }

    public static void openAppWithUriScheme(Context context, String uri_scheme, String packageName, boolean isSingltTask) {
        try {
            Log.d("linkedme", "openAppWithUriScheme: uri scheme ==== " + uri_scheme);
            if (TextUtils.isEmpty(uri_scheme)) {
                Toast.makeText(context, "Uri Scheme不能为空！", Toast.LENGTH_SHORT).show();
            } else if (uri_scheme.contains(" ")) {
                Toast.makeText(context, "Uri Scheme中含有空格！", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri_scheme));
                if (!TextUtils.isEmpty(packageName)) {
                    if (packageName.contains(" ")) {
                        Toast.makeText(context, "packageName中含有空格！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent.setPackage(packageName);
                }
                if (isSingltTask) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                if (context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                    Toast.makeText(context, "正在跳转...", Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);
                    return;
                }
                Toast.makeText(context, "无可处理该Uri Scheme的APP，无法唤起", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Uri Scheme解析异常，无法唤起APP", Toast.LENGTH_SHORT).show();
        }
    }
}