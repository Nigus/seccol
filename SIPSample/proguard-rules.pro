# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:/android-studio/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
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
-dontwarn butterknife.**
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-dontwarn android.support.**
-keep class com.google.android.gms.** { *; }
 -dontwarn java.nio.file.Files
 -dontwarn java.nio.file.Path
 -dontwarn java.nio.file.OpenOption
 -dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
 -dontwarn com.activate.gcm.**
 -dontwarn com.android.volley.**
 -dontwarn com.sun.mail.**
 -dontwarn org.spongycastle.**
 -dontwarn javax.mail.**