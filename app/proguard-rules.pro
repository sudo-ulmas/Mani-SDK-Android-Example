# Keep Flutter classes
-keep class io.flutter.** { *; }
-dontwarn io.flutter.**

# Keep all Flutter plugins
-keep class io.flutter.plugins.** { *; }
-dontwarn io.flutter.plugins.**

# Keep your specific plugin (replace with your plugin's package)
-keep class com.yourplugin.package.** { *; }
-dontwarn com.yourplugin.package.**

# Keep all plugin registrants
-keep class io.flutter.plugin.common.** { *; }
-keep class androidx.lifecycle.DefaultLifecycleObserver

# Keep native method names
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep plugin registration classes
-keep class * extends io.flutter.plugin.common.*