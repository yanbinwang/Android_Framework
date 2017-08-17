-dontshrink
#指定代码的压缩级别
-optimizationpasses 5
  
#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
  
#优化  不优化输入的类文件
-dontoptimize
  
#不做预校验
-dontpreverify
  
#混淆时是否记录日志
-verbose
  
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
  
#保护注解
-keepattributes *Annotation*
  
# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
#如果有引用v7包可以添加下面这行
-keep public class * extends android.support.v7.app.AppCompatActivity
  
#忽略警告
-ignorewarning
  
##记录生成的日志数据,gradle build时在本项目根目录输出##
  
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt
  
########记录生成的日志数据，gradle build时 在本项目根目录输出-end######
  
  
#####混淆保护自己项目的部分代码以及引用的第三方jar包library#######
  
#-libraryjars libs/umeng-analytics-v5.2.4.jar
  
#三星应用市场需要添加:sdk-v1.0.0.jar,look-v1.0.1.jar
#-libraryjars libs/sdk-v1.0.0.jar
#-libraryjars libs/look-v1.0.1.jar

#如果不想混淆 keep 掉
-keep class com.lippi.recorder.iirfilterdesigner.** {*; }
#项目特殊处理代码
  
#忽略警告
-dontwarn com.lippi.recorder.utils**
#保留一个完整的包
-keep class com.lippi.recorder.utils.** {
    *;
 }
  
-keep class  com.lippi.recorder.utils.AudioRecorder{*;}
  
  
#如果引用了v4或者v7包
-dontwarn android.support.**
  
####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####
  
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
  
#保持 native 方法不被混淆
#           -keepclasseswithmembernames class * {  
#   native <methods>;
#           }  
  
# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}
  
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
  
#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
  
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
  
#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
  
#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
  
#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
#-keepclassmembers enum * {
#  public static **[] values();
#  public static ** valueOf(java.lang.String);
#}
  
-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
  
#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#------------------------h5混淆开始------------------------
#不混淆H5交互
-keepattributes *JavascriptInterface*

#ClassName是类名，H5_Object是与javascript相交互的object，建议以内部类形式书写
-keepclassmembers   class **.ClassName$H5_Object{
    *;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
#------------------------h5混淆结束------------------------

#------------------------glide图片库混淆开始------------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#------------------------glide图片库混淆结束------------------------

#------------------------原生NoHttp混淆开始------------------------
-dontwarn com.yanzhenjie.nohttp.**
-keep class com.yanzhenjie.nohttp.**{*;}
#------------------------原生NoHttp混淆结束------------------------

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

##---------------End: proguard configuration for Gson  ----------

#------------------------映射混淆开始------------------------#
# keep annotated by NotProguard
-keep @com.wyb.iocframe.annotation.ViewInject class * {*;}
-keep class * {
@com.wyb.iocframe.annotation.ViewInject <fields>;
}
-keepclassmembers class * {
@com.wyb.iocframe.annotation.ViewInject <methods>;
}
#------------------------映射混淆结束------------------------#

#------------------------百度地图混淆开始------------------------#
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}
#------------------------百度地图混淆结束------------------------#