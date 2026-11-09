package com.calorix.app

import android.app.Application
import com.calorix.app.data.CalorieDatabase
import com.calorix.app.data.FoodRepository
import com.calorix.app.data.UserPreferencesRepository

class CalorixApplication : Application() {

    val database: CalorieDatabase by lazy {
        CalorieDatabase.getDatabase(this)
    }

    val preferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(this)
    }

    val foodRepository: FoodRepository by lazy {
        FoodRepository(database.foodDao(), preferencesRepository)
    }
}
