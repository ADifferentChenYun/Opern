# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Yun\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#--------------------weibo sdk--------------------------------------------------------
-keep class com.sina.weibo.sdk.** { *; }

#--------------------bugly------------------------------------------------------------
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}

#--------------------glide------------------------------------------------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

#--------------------event-------------------------------------------------------------
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#--------------------retrofit------------------------------------------------------------
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-dontwarn okio.**

-dontwarn okhttp3.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

#----------------------Gson---还未完善-----------------------------------------------------------
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod
#-keep class org.xz_sale.entity.**{*;}

#--------------------banner--------------------------------------------------------------
-keep class com.youth.banner.** {
    *;
 }

 #-------------------avloadingview--------------------------------------------------------
 -keep class com.wang.avi.** { *; }
 -keep class com.wang.avi.indicators.** { *; }

 #-------------------jpush----------------------------------------------------------------
 -dontoptimize
 -dontpreverify

 -dontwarn cn.jpush.**
 -keep class cn.jpush.** { *; }
 -keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

 -dontwarn cn.jiguang.**
 -keep class cn.jiguang.** { *; }

 #------------------greenDao----------------------------------------------------------
 -keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
 public static java.lang.String TABLENAME;
 }
 -keep class **$Properties

 # If you do not use SQLCipher:
 -dontwarn org.greenrobot.greendao.database.**
 # If you do not use Rx:
 -dontwarn rx.**