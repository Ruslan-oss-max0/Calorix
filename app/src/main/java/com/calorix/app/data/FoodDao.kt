package com.calorix.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    @Query("SELECT * FROM food_entries WHERE dateString = :dateString ORDER BY timestamp DESC")
    fun getEntriesForDate(dateString: String): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<FoodEntry>>

    @Query("SELECT SUM(calories) FROM food_entries WHERE dateString = :dateString")
    fun getTotalCaloriesForDate(dateString: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FoodEntry): Long

    @Delete
    suspend fun deleteEntry(entry: FoodEntry): Int

    @Query("DELETE FROM food_entries WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM food_entries")
    suspend fun clearAll(): Int
}
