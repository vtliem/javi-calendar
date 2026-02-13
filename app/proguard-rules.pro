# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# Kotlinx Serialization
-keepattributes *Annotation*, Enums
-keepclassmembers class com.vtl.javicalendar.** {
    *** Companion;
    *** $serializer;
}
-keep @kotlinx.serialization.Serializable class com.vtl.javicalendar.** { *; }
-keepclassmembers class com.vtl.javicalendar.** {
    @kotlinx.serialization.SerialName <fields>;
}

# Jetpack Compose
-keepclassmembers class  ** {
    @androidx.compose.runtime.Composable *;
}

# WorkManager
-keep class * extends androidx.work.ListenableWorker {
    <init>(android.content.Context, androidx.work.WorkerParameters);
}

# Keep members of specific models if they are used for serialization or reflection
-keep class com.vtl.javicalendar.domain.model.** { *; }
-keep class com.vtl.javicalendar.presentation.model.** { *; }
