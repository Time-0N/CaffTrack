package com.example.cafftrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cafftrack.viewmodel.CaffeineViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import com.example.cafftrack.model.enums.Sex


@Composable
fun HomeScreen(viewModel: CaffeineViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var caffeineMg by remember { mutableStateOf("") }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showEntryDialog by remember { mutableStateOf(false) }

    val caffeineLevel by viewModel.currentCaffeineInBlood.collectAsState()

    // Icon + CaffeineLevel Display
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { showProfileDialog = true },
            modifier = Modifier.size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Edit Profile",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (caffeineLevel == -1.0) {
            Text("Please configure your profile first!", color = MaterialTheme.colorScheme.error)
        } else {
            Text(
                text = "Caffeine in blood: ${"%.1f".format(caffeineLevel)} mg",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Button(
            onClick = { showEntryDialog = true },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Add Entry")
        }
    }

    // Dialog
    if (showProfileDialog) {
        var weightKg by remember { mutableStateOf("") }
        var selectedSex by remember { mutableStateOf(Sex.Male) }

        AlertDialog(
            onDismissRequest = {
                showProfileDialog = false
                weightKg = ""
                selectedSex = Sex.Male
            },
            confirmButton = {
                TextButton(onClick = {
                    val weight = weightKg.toIntOrNull()
                    if (weight != null) {
                        viewModel.saveProfile(weight, selectedSex)
                        showProfileDialog = false
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showProfileDialog = false
                    weightKg = ""
                    selectedSex = Sex.Male
                }) {
                    Text("Cancel")
                }
            },
            title = { Text("Edit Profile") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextField(
                        value = weightKg,
                        onValueChange = { weightKg = it },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Sex.values().forEach { sex ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedSex == sex,
                                    onClick = { selectedSex = sex }
                                )
                                Text(sex.name)
                            }
                        }
                    }
                }
            }
        )
    }

    //CaffeineEntryDialog
    if (showEntryDialog) {
        AlertDialog(
            onDismissRequest = {
                showEntryDialog = false
                name = ""
                caffeineMg = ""
            },
            title = { Text("New Caffeine Entry") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Drink Name") }
                    )

                    TextField(
                        value = caffeineMg,
                        onValueChange = { caffeineMg = it },
                        label = { Text("Caffeine (mg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val caffeineValue = caffeineMg.toIntOrNull()
                        if (name.isNotBlank() && caffeineValue != null) {
                            viewModel.insertEntry(name, caffeineValue)
                            showEntryDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showEntryDialog = false
                    name = ""
                    caffeineMg = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }


}
