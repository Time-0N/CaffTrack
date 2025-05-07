package com.example.cafftrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cafftrack.viewmodel.CaffeineViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment

@Composable
fun HomeScreen(viewModel: CaffeineViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var caffeineMg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Drink name") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = caffeineMg,
            onValueChange = { caffeineMg = it },
            label = { Text("Caffeine (mg)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                if (name.isNotBlank() && caffeineMg.isNotBlank()) {
                    viewModel.insertEntry(
                        name = name,
                        caffeineMg = caffeineMg.toIntOrNull() ?: 0
                    )
                    name = ""
                    caffeineMg = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Log it")
        }
    }
}
