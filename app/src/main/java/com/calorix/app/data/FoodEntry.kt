package com.calorix.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val calories: Int,
    val mealType: MealType,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String = getFormattedDate(timestamp) // Format: "yyyy-MM-dd" for grouping
) {
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val displayDateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        fun getFormattedDate(timeMillis: Long): String {
            return dateFormat.format(Date(timeMillis))
        }

        fun getDisplayDate(dateString: String): String {
            return try {
                val parsed = dateFormat.parse(dateString)
                val today = dateFormat.format(Date())
                val yesterday = dateFormat.format(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))
                
                when (dateString) {
                    today -> "Today"
                    yesterday -> "Yesterday"
                    else -> parsed?.let { displayDateFormat.format(it) } ?: dateString
                }
            } catch (e: Exception) {
                dateString
            }
        }

        fun getDisplayTime(timeMillis: Long): String {
            return timeFormat.format(Date(timeMillis))
        }
    }
}
