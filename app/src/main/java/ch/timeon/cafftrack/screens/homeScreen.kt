package ch.timeon.cafftrack.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import ch.timeon.cafftrack.viewmodel.CaffeineViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import ch.timeon.cafftrack.model.enums.Sex
import kotlinx.coroutines.flow.collectLatest


@Composable
fun homeScreen(
    modifier: Modifier = Modifier,
    viewModel: CaffeineViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var caffeineMg by remember { mutableStateOf("") }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showEntryDialog by remember { mutableStateOf(false) }
    var context = LocalContext.current

    val caffeineLevel by viewModel.currentCaffeineInBlood.collectAsState()
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.getStringExtra("UPC")?.let { upc ->
            viewModel.lookupAndLog(upc)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorMessages.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

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

        Spacer(Modifier.height(8.dp))

        // BarcodeScanner
        Button(
            onClick = {
                scanLauncher.launch(
                    Intent(context, BarcodeScannerActivityScreen::class.java)
                )
            },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Scan barcode",
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Scan")
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
