# OAID SDK集成说明

## 1. Android 10 OAID 描述

> Android 10 无法通过常规方式获取IMEI等设备ID，影响广告的正常加载、展示等业务，开发者需要接入 OAID SDK 才可以正常加载广告。建议开发者优先集成OAID，集成方式可以参看 MSA联盟官网地址: [http://www.msa-alliance.cn/col.jsp?id=120](http://www.msa-alliance.cn/col.jsp?id=120)
> 


## 2. 引入依赖

>把 oaid_sdk_x.x.x.aar 拷贝到项的 libs 目录，并设置依赖，其中 x.x.x 代表版本号。

```
implementation files('libs/oaid_sdk_x.x.x.aar')
```

## 3. Assets设置

* 将 supplierconfig.json 拷贝到项目 assets 目录下，并修改里边对应内容，特别是需要设置 appid 的部分。需要设置 appid 的部分需要去对应厂商的应用商店里注册自己的app。
* 将证书文件(**应用包名.cert.pem**)拷贝到项目 app/src/main/assets 目录下或在SDK初始化前通过 **GtAdSdk.setOAIDCertPem(certpemStr)** 传入OAID证书Pem内容。
* **仅1.0.26+版本需要配置证书**。
  
  > 证书需要填写 example_batch.csv 后发送到 msa@caict.ac.cn 申请，注意每个包名对应一个签名，申请时需要将所需申请的全部包名填写到表格中。为避免证书过期影响 APP 获取 ID，建议证书信息可以从后台服务器获取，或 者当调用 oaid SDK 接口提示证书无效时，调用后台接口及时更新证书信息，并且 快到期时及时提前重新申请证书。

## 4. gradle 配置

> 设置 gradle 编译选项，这里可以根据自己对平台的选择进行合理配置。

```
ndk {
  abiFilters 'armeabi-v7a','x86','arm64-v8a','x86_64','armeabi'
}

packagingOptions {
    doNotStrip "*/armeabi-v7a/*.so"
    doNotStrip "*/x86/*.so"
    doNotStrip "*/arm64-v8a/*.so"
    doNotStrip "*/x86_64/*.so"
    doNotStrip "armeabi.so"
}
```

## 5. 混淆设置

> 现SDK支持oaid_sdk_1.0.10、1.0.13、1.0.22、1.0.23、1.0.25, 1.0.30, 1.1.0, 1.2.0, 1.2.1, 2.0.0, 2.1.0,2.2.0等版本、但是媒体必须按照以下混淆配置进行设置

```java
# 1.0.25之后

# sdk
-keep class com.bun.miitmdid.** { *; }
-keep interface com.bun.supplier.** { *; }

# asus
-keep class com.asus.msa.SupplementaryDID.** { *; }
-keep class com.asus.msa.sdid.** { *; }
# freeme
-keep class com.android.creator.** { *; }
-keep class com.android.msasdk.** { *; }
# huawei
-keep class com.huawei.hms.ads.** { *; }
-keep interface com.huawei.hms.ads.** {*; }
# lenovo
-keep class com.zui.deviceidservice.** { *; }
-keep class com.zui.opendeviceidlibrary.** { *; }
# meizu
-keep class com.meizu.flyme.openidsdk.** { *; }
# nubia
-keep class com.bun.miitmdid.provider.nubia.NubiaIdentityImpl { *; }
# oppo
-keep class com.heytap.openid.** { *; }
# samsung
-keep class com.samsung.android.deviceidservice.** { *; }
# vivo
-keep class com.vivo.identifier.** { *; }
# xiaomi
-keep class com.bun.miitmdid.provider.xiaomi.IdentifierManager { *; }
# zte
-keep class com.bun.lib.** { *; }
# coolpad
-keep class com.coolpad.deviceidsupport.** { *; }

# 1.0.25 之前
-dontwarn com.bun.**
-keep class com.bun.** {*;}
-keep class a.**{*;}
-keep class XI.CA.XI.**{*;}
-keep class XI.K0.XI.**{*;}
-keep class XI.XI.K0.**{*;}
-keep class XI.vs.K0.**{*;}
-keep class XI.xo.XI.XI.**{*;}
-keep class com.asus.msa.SupplementaryDID.**{*;}
-keep class com.asus.msa.sdid.**{*;}
-keep class com.huawei.hms.ads.identifier.**{*;}
-keep class com.samsung.android.deviceidservice.**{*;}
-keep class com.zui.opendeviceidlibrary.**{*;}
-keep class org.json.**{*;}
-keep public class com.netease.nis.sdkwrapper.Utils {public <methods>;}
```

## 6.错误码

> 信息码（引用 InfoCode 类）

| 信息 | 值 | 说明                                                         |
| --- | --- | --- |
| INIT_INFO_RESULT_OK |1008610			|调用成功，获取接口是同步的|
| INIT_INFO_RESULT_DELAY            |1008614			|调用成功，获取接口是异步的|
| INIT_ERROR_CERT_ERROR |1008616			|证书未初始化或证书无效|
| INIT_ERROR_MANUFACTURER_NOSUPPORT |1008611			|不支持的厂商|
| INIT_ERROR_DEVICE_NOSUPPORT |1008612			|不支持的设备|
| INIT_ERROR_LOAD_CONFIGFILE |1008613			|加载配置文件出错|
| INIT_ERROR_SDK_CALL_ERROR |1008615			|sdk调用出错|

## 7.如何检验oaid接入成功

> Android Studio 控制台里使用logcat 过滤一下MdIdSdkHelper 关键字。