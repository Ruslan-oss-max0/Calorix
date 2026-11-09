package com.calorix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.calorix.app.ui.navigation.AppNavigation
import com.calorix.app.ui.theme.CalorixTheme
import com.calorix.app.ui.viewmodel.CalorieViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CalorieViewModel by viewModels {
        val app = application as CalorixApplication
        CalorieViewModel.Factory(app.foodRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalorixTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
