package com.calorix.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calorix.app.data.MealType
import com.calorix.app.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    onSaveFood: (name: String, calories: Int, mealType: MealType) -> Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var foodName by remember { mutableStateOf("") }
    var caloriesText by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf(MealType.LUNCH) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var caloriesError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    fun validateAndSubmit() {
        val trimmedName = foodName.trim()
        val parsedCalories = caloriesText.toIntOrNull()

        var hasError = false

        if (trimmedName.isEmpty()) {
            nameError = "Food name is required"
            hasError = true
        } else {
            nameError = null
        }

        if (parsedCalories == null || parsedCalories <= 0) {
            caloriesError = "Enter valid calories (> 0)"
            hasError = true
        } else {
            caloriesError = null
        }

        if (!hasError && parsedCalories != null) {
            val success = onSaveFood(trimmedName, parsedCalories, selectedMealType)
            if (success) {
                onNavigateBack()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Food Entry",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Food Name Field Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Food Details",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = foodName,
                        onValueChange = {
                            foodName = it
                            if (nameError != null && it.trim().isNotEmpty()) {
                                nameError = null
                            }
                        },
                        label = { Text("Food or Dish Name") },
                        placeholder = { Text("e.g. Grilled Chicken Salad") },
                        isError = nameError != null,
                        supportingText = {
                            nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = caloriesText,
                        onValueChange = { input ->
                            // Only allow digits
                            if (input.all { it.isDigit() }) {
                                caloriesText = input
                                if (caloriesError != null && (input.toIntOrNull() ?: 0) > 0) {
                                    caloriesError = null
                                }
                            }
                        },
                        label = { Text("Calories (kcal)") },
                        placeholder = { Text("e.g. 450") },
                        isError = caloriesError != null,
                        supportingText = {
                            caloriesError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                validateAndSubmit()
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Calorie Increment Presets
                    Text(
                        text = "Quick Presets",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(100, 250, 400, 500).forEach { preset ->
                            OutlinedButton(
                                onClick = {
                                    caloriesText = "$preset"
                                    caloriesError = null
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "+$preset", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Meal Type Selector Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Select Meal Type",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MealType.entries.forEach { meal ->
                            val isSelected = selectedMealType == meal
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedMealType = meal },
                                label = { Text(meal.displayName, fontSize = 12.sp) },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Outlined.Check,
                                            contentDescription = null,
                                            modifier = Modifier.height(16.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary.copy(alpha = 0.2f),
                                    selectedLabelColor = EmeraldPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { validateAndSubmit() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary
                    )
                ) {
                    Text(
                        text = "Save Food Entry",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Cancel")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
