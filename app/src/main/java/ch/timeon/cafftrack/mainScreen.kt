package ch.timeon.cafftrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ch.timeon.cafftrack.screens.homeScreen
import ch.timeon.cafftrack.screens.logScreen
import ch.timeon.cafftrack.screens.statsScreen
import ch.timeon.cafftrack.ui.graphics.vector.imageVector.TabItem


@Composable
fun mainScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        TabItem("Home", Icons.Filled.Home),
        TabItem("Log",  Icons.Filled.Coffee),
        TabItem("Stats",Icons.Filled.History)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar {
                    tabs.forEachIndexed { idx, tab ->
                        NavigationBarItem(
                            icon = { Icon(tab.icon, tab.title) },
                            label = { Text(tab.title) },
                            selected = idx == selectedTabIndex,
                            onClick  = { selectedTabIndex = idx }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(innerPadding)
            ) {
                when (selectedTabIndex) {
                    0 -> homeScreen(modifier = Modifier.fillMaxSize())
                    1 -> logScreen(modifier = Modifier.fillMaxSize())
                    2 -> statsScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}