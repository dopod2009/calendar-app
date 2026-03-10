# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in Android SDK tools/proguard/proguard-android-optimize.txt

# Keep application classes
-keep public class com.calendar.app.** { *; }

# Keep data models
-keep class com.calendar.core.domain.model.** { *; }
-keep class com.calendar.core.data.model.** { *; }

# Keep Room generated classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
