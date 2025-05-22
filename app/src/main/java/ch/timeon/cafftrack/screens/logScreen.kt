package ch.timeon.cafftrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ch.timeon.cafftrack.ui.components.swipeToDeleteItem
import ch.timeon.cafftrack.viewmodel.CaffeineViewModel
import java.util.*

@Composable
fun logScreen(
    modifier: Modifier = Modifier,
    viewModel: CaffeineViewModel = hiltViewModel()
) {
    val entries by viewModel.caffeineEntries.collectAsState()

    LazyColumn {
        items(entries, key = { it.id }) { entry ->
            swipeToDeleteItem(
                onDelete = { viewModel.delete(entry) }
            ) {
                ListItem(
                    headlineContent = { Text(entry.name) },
                    supportingContent = {
                        Text("${entry.caffeineMg}mg @ ${Date(entry.timestamp)}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
            Divider()
        }
    }
}