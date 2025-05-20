# 忽略OPPO/ColorOS系统特定类
-dontwarn android.app.OplusNotificationManager
-dontnote android.app.OplusNotificationManager

# 忽略ID提供者实现类
-dontwarn com.android.id.impl.IdProviderImpl
-dontnote com.android.id.impl.IdProviderImpl

-keep class com.heytap.openid.** { *; }
-keep class com.android.id.** { *; }