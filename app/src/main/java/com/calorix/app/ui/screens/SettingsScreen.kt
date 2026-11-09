package com.calorix.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calorix.app.ui.theme.CoralRed
import com.calorix.app.ui.theme.EmeraldPrimary
import com.calorix.app.ui.viewmodel.CalorieUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: CalorieUiState,
    onSaveGoal: (Int) -> Boolean,
    onClearAllData: () -> Unit,
    onClearFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    var goalInput by remember(uiState.dailyGoal) {
        mutableStateOf(uiState.dailyGoal.toString())
    }
    var inputError by remember { mutableStateOf<String?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.userFeedbackMessage) {
        uiState.userFeedbackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onClearFeedback()
        }
    }

    fun submitGoal() {
        val parsed = goalInput.toIntOrNull()
        if (parsed == null || parsed < 500 || parsed > 10000) {
            inputError = "Enter a realistic goal between 500 and 10,000 kcal"
        } else {
            inputError = null
            focusManager.clearFocus()
            onSaveGoal(parsed)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Goal & Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            // Daily Calorie Goal Card
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
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Flag,
                            contentDescription = null,
                            tint = EmeraldPrimary
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = "Daily Calorie Target",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Customize your personal daily calorie intake target. This directly adjusts your progress ring on the Today dashboard.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) {
                                goalInput = input
                                if (inputError != null) inputError = null
                            }
                        },
                        label = { Text("Daily Target (kcal)") },
                        placeholder = { Text("e.g. 2000") },
                        isError = inputError != null,
                        supportingText = {
                            inputError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { submitGoal() }),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preset buttons
                    Text(
                        text = "Common Targets",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1500, 1800, 2000, 2200, 2500).forEach { preset ->
                            OutlinedButton(
                                onClick = {
                                    goalInput = "$preset"
                                    inputError = null
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "$preset", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { submitGoal() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.Check, contentDescription = null)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text("Update Daily Goal")
                    }
                }
            }

            // Architecture & Database Info Card
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
                        .padding(20.dp)
                ) {
                    Text(
                        text = "App Architecture & Storage",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Architecture: MVVM (Model-View-ViewModel) + StateFlow\n" +
                                "• UI Framework: Jetpack Compose (Material 3)\n" +
                                "• Local Storage: Room SQLite Database with Coroutines Flow\n" +
                                "• Target SDK: 35 (Android 15), Min SDK: 24 (Android 7.0)\n" +
                                "• Reactive live updates across all screens",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Reset Data Card
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
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Demo & Reset Actions",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Clear all logged entries to test empty state scenarios or restart video recording from scratch.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CoralRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Outlined.DeleteForever, contentDescription = null)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text("Clear All Food Logs")
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Clear All Food Entries?") },
            text = { Text("This will permanently delete all logged entries from the local Room database.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
