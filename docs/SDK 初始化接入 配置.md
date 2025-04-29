# SDK初始化说明

## 1. 准备工作

* 创建应用并拿到SDK所需的参数AppId、广告位AdUnitID 等,向广推商务获取
* 把 gt-release.aar 放入app的libs工程中
* SDK 支持 Android API Level 21+

## 2. Demo示例

[点击查看 ADSDK  Demo](https://github.com/one-piece-official/GtAndroidSdkDemo)


## 3. 添加依赖
```java

dependencies {
    implementation fileTree(include: ['*.aar'], dir: 'libs')
    
    // 媒体 必须 要依赖下面这 2个库，否则 华为和荣耀设备 无法拿到OAID,严重影响广告填充
     implementation "com.huawei.hms:ads-identifier:3.4.62.300"
     implementation 'com.hihonor.mcs:ads-identifier:1.0.2.301'
}

```


## 4. SDK初始化配置

```java
        Map<String, Object> customData = new HashMap<>();
        customData.put("custom_key", "custom_value");

        GTAdSdk.getInstance().init(this, new GtSdkConfig.Builder()
                .appId("")       //必填，向广推商务获取
                .userId("")      // 用户ID，有就填
                .debugEnv(false)  // 是否使用测试环境域名 请求广告，正式环境务必为false
                .showLog(false)   // 是否展示adsdk内部日志，正式环境务必为false
                .addCustomData(customData)  //自定义数据
                .customController(new GtCustomController() {
                    // 是否允许SDK获取位置信息
                    @Override
                    public boolean canReadLocation() {
                        return true;
                    }

                    // 是否允许SDK获取手机状态地信息，如：imei deviceid
                    @Override
                    public boolean canUsePhoneState() {
                        return true;
                    }
                    
                    // 是否允许SDK使用AndoridId
                    @Override
                    public boolean canUseAndroidId() {
                        return true;

                    }
                    // 是否允许SDK写外部数据存储
                    @Override
                    public boolean canUseWriteExternal() {
                        return true;
                    }
                    // 是否允许SDK获取应用安装列表
                    @Override
                    public boolean canReadInstalledPackages() {
                        return true;
                    }
                    // 是否允许SDK获取Wifi状态
                    @Override
                    public boolean canUseWifiState() {
                        return true;
                    }
                    // 为SDK提供oaid
                    @Override
                    public String getOaid() {
                        return "";
                    }
                })
                .setInitCallback(new GtInitCallback() {
                    // 初始化成功回调，初始化成功后才可以加载广告
                    @Override
                    public void onSuccess() {
                        Log.d(Constants.LOG_TAG, "init--------------onSuccess-----------");
                    }

                    // 初始化失败回调
                    @Override
                    public void onFail(int code, String msg) {
                        Log.d(Constants.LOG_TAG, "init--------------onFail-----------" + code + ":" + msg);
                    }
                }).build());

        // 个性化广告开关设置
        GTAdSdk.getInstance().setPersonalizedAdvertisingOn(true);
```

## 5 华为荣耀 OAID maven仓库
 repositories 中 增加如下配置

```
 maven { url 'https://developer.huawei.com/repo' }
 maven { url 'https://developer.hihonor.com/repo' }
```

###  2 初始化相关类说明

####  1. GtSdkConfig
> com.gt.sdk.api.GtSdkConfig.Builder

| 方法名 | 方法介绍 |
| --- | --- |
| appId(String appId) | 初始化sdk需要的参数。                    |
| debug(boolean isLog) | sdk是否开启debug日志打印信息，默认开启。    |
| userId(String userId) | 用户Id(非必填)。                     |
| addCustomData(Map<String, String> customData) | 初始化传入的自定义数据。 |
| customController(GtCustomController custom) | 设置自定义设备信息，具体可参考下方GtCustomController实体类。|
| setInitCallback(GtInitCallback callBack) | 初始化回调通知，具体可参考GtInitCallback实体类。 |


####  2. GtCustomController
> com.gt.sdk.api.GtCustomController

| 方法名 | 方法介绍 |
| --- | --- |
| canReadLocation() | sdk是否可以读取设备位置信息。                    |
| canUsePhoneState() | sdk是否可以获取设备IMEI等信息。                     |
| canUseAndroidId()  | sdk是否可以获取设备AndroidID。|
| canUseWriteExternal() | sdk是否可以读写外置存储卡                    |
| canReadInstalledPackages() | sdk是否可以读取设备已安装app列表。 |
| canUseWifiState() | sdk是否可以获取设备WIFI状态等信息。                     |
| String getOaid()  | 媒体可以自行传入OAID。|

####  3. GtInitCallback
> com.gt.sdk.api.GtInitCallback

| 方法名 | 方法介绍 |
| --- | --- |
| void onSuccess() | sdk初始化成功回调。                    |
| void onFail(int code, String msg) | sdk初始化失败回调。 |



### 4. 个性化广告设置（可选）

```java
GTAdSdk.getInstance().setPersonalizedAdvertisingOn( boolean personal);    
```
