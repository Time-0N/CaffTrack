package com.example.cafftrack.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cafftrack.viewmodel.CaffeineViewModel

@Composable
fun StatsScreen(viewModel: CaffeineViewModel = hiltViewModel()) {
    val average by viewModel.averageCaffeineLast7Days.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "7-Day Average Caffeine Intake",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "$average mg / day",
            style = MaterialTheme.typography.titleLarge
        )
    }
}