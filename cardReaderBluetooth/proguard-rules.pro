# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
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
-keepattributes *Annotation* #忽略警告
-ignorewarnings
-dontwarn net.poemcode.**
-dontskipnonpubliclibraryclassmembers

-keep public class * extends android.app.Service # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference # 保持哪些类不被混淆
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**

-libraryjars 'D:\Program Files\Java\jdk1.7.0_79\jre\lib\rt.jar'
-libraryjars 'D:\Sdk\platforms\android-19\android.jar'

-optimizationpasses 5
-dontusemixedcaseclassnames

#gson
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *;}
-keepclassmembers class * implements java.io.Serializable {
 static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    public <fields>;
}

-keep class com.hexing.cardReaderBluetooth.bean.**
-keep class com.hexing.cardReaderBluetooth.api.**

-dontwarn com.acs.bluetooth.**
-keep class com.acs.bluetooth.**{*;}

#下面的类将不会被混淆，这样的类是需要被jar包使用者直接调用的
-keep public class com.hexing.carReaderBluetooth {
    public <fields>;
    public <methods>;
}

-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt

