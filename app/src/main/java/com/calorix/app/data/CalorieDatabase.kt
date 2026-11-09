package com.calorix.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromMealType(value: MealType): String {
        return value.name
    }

    @TypeConverter
    fun toMealType(value: String): MealType {
        return MealType.fromString(value)
    }
}

@Database(entities = [FoodEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CalorieDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao

    companion object {
        @Volatile
        private var INSTANCE: CalorieDatabase? = null

        fun getDatabase(context: Context): CalorieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalorieDatabase::class.java,
                    "calorix_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
