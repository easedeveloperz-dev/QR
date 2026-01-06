# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ==========================================
# OPTIMIZATION FLAGS
# ==========================================
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''

# ==========================================
# APP CLASSES
# ==========================================

# Keep model classes for Room and Gson serialization
-keep class aki.pawar.qr.data.local.entity.** { *; }
-keep class aki.pawar.qr.domain.model.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepattributes *Annotation*

# ==========================================
# ML KIT - Only keep what's needed
# ==========================================
-keep class com.google.mlkit.vision.barcode.** { *; }
-dontwarn com.google.mlkit.**

# ==========================================
# ZXING - Only keep core encoding
# ==========================================
-keep class com.google.zxing.qrcode.QRCodeWriter { *; }
-keep class com.google.zxing.common.BitMatrix { *; }
-keep class com.google.zxing.BarcodeFormat { *; }
-keep class com.google.zxing.EncodeHintType { *; }
-dontwarn com.google.zxing.**

# ==========================================
# ROOM DATABASE
# ==========================================
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# ==========================================
# CAMERAX
# ==========================================
-keep class androidx.camera.core.** { *; }
-keep class androidx.camera.camera2.** { *; }
-keep class androidx.camera.lifecycle.** { *; }
-keep class androidx.camera.view.** { *; }
-dontwarn androidx.camera.**

# ==========================================
# COROUTINES
# ==========================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ==========================================
# FIREBASE
# ==========================================
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# ==========================================
# COMPOSE - Remove unused
# ==========================================
-dontwarn androidx.compose.material.icons.**

# ==========================================
# GENERAL OPTIMIZATIONS
# ==========================================
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn javax.naming.**

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Remove Kotlin null checks in release for smaller size
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkNotNull(...);
    public static void checkNotNullParameter(...);
    public static void checkParameterIsNotNull(...);
    public static void checkNotNullExpressionValue(...);
    public static void checkExpressionValueIsNotNull(...);
    public static void checkReturnedValueIsNotNull(...);
}
