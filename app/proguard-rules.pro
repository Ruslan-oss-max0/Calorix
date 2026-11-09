# Proguard rules for Room and Coroutines
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**
-keep class com.calorix.app.data.** { *; }
