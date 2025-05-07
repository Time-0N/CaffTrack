package com.example.cafftrack.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cafftrack.viewmodel.CaffeineViewModel
import java.util.Date


@Composable
fun LogScreen(viewModel: CaffeineViewModel = hiltViewModel()) {
    val entries by viewModel.caffeineEntries.collectAsState()

    LazyColumn {
        items(entries) { entry ->
            ListItem(
                headlineContent = { Text(entry.name) },
                supportingContent = { Text("${entry.caffeineMg}mg @ ${Date(entry.timestamp)}") }
            )
            Divider()
        }
    }
}
