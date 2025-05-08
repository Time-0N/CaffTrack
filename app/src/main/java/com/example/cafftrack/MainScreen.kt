package com.example.cafftrack

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.cafftrack.screens.HomeScreen
import com.example.cafftrack.screens.LogScreen
import com.example.cafftrack.screens.StatsScreen
import com.example.cafftrack.ui.graphics.vector.ImageVector.TabItem


@Composable
fun MainScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        TabItem("Home", Icons.Filled.Home),
        TabItem("Log", Icons.Filled.Coffee),
        TabItem("Stats", Icons.Filled.History)
    )

    Scaffold(
        modifier = Modifier
            .padding(WindowInsets.systemBars.asPaddingValues()),
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed{ index, tab ->
                    NavigationBarItem(
                        selected = index == selectedTabIndex,
                        onClick = { selectedTabIndex = index },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title)}
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTabIndex) {
            0 -> HomeScreen(modifier = Modifier.padding(innerPadding))
            1 -> LogScreen(modifier = Modifier.padding(innerPadding))
            2 -> StatsScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}