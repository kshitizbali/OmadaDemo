# ProGuard/R8 rules for OmadaDemo app
#
# Optimization Strategy:
# - Aggressive obfuscation of non-critical classes
# - Preservation of public API and reflection-based classes
# - Optimization passes for APK size reduction
# - Crash reporting line number mapping maintained
#

# ============ OPTIMIZATION CONFIGURATION ============

# Enable optimization passes for better code shrinking
-optimizationpasses 5

# Keep specific optimizations for performance
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# Disable inlining for better debugging in stack traces
-dontinline

# ============ KEEP RULES ============

# Keep BuildConfig
-keep class com.example.omadademo.BuildConfig { *; }

# Keep all model classes
-keep class com.example.omadademo.data.remote.dto.** { *; }
-keep class com.example.omadademo.domain.model.** { *; }

# Retrofit
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep interface retrofit2.** { *; }
-keep class * implements retrofit2.Converter { *; }
-keepclasseswithmembers class * {
    @retrofit2.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# Moshi
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep interface dagger.hilt.** { *; }
-keepclassmembers class * {
    @dagger.hilt.* <fields>;
    @dagger.hilt.* <methods>;
}

# Coroutines
-keepclassmembernames class kotlinx.coroutines.internal.MainDispatcherFactory {
    *** factory;
}
-keepclassmembernames class kotlinx.coroutines.CoroutineExceptionHandler {
    *** handler;
}

# Keep line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep Fragment subclasses
-keep class * extends androidx.fragment.app.Fragment { *; }

# Keep ViewModel subclasses
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Coil
-keep class coil.** { *; }
-keep interface coil.** { *; }

# Timber
-keep class timber.log.** { *; }

# ============ ADDITIONAL OPTIMIZATION ============

# Aggressive obfuscation of internal classes
-allowaccessmodification
-mergeinterfacesaggressively

# Remove unused resources and classes
-dontshrink  # Comment out if you want aggressive shrinking

# Rename source file attribute for smaller APK
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Keep custom exception classes with full names for catching
-keep class com.example.omadademo.domain.exception.PhotoException { *; }
-keep class com.example.omadademo.domain.exception.PhotoException$* { *; }

# ============ LOGGING AND DEBUGGING ============

# Keep exception stack traces readable
-keepattributes Exceptions
-keepattributes InnerClasses,EnclosingMethod

# Keep annotations for Hilt, Retrofit, etc.
-keepattributes *Annotation*
-keepattributes Signature

# ============ KOTLIN SPECIFIC ============

# Kotlin metadata for reflection
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations
-keep class kotlin.Metadata { *; }

# Keep companion object classes
-keep class **$Companion { *; }

# ============ WARNING SUPPRESSION ============

# Suppress warnings for unavailable libraries (not critical)
-dontwarn javax.lang.model.**
-dontwarn sun.reflect.**