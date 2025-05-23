package ch.timeon.cafftrack.screens

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.timeon.cafftrack.ui.cameraPreviewView


class BarcodeScannerActivityScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var hasCamera by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        this, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }
            val launcher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
                hasCamera = granted
            }
            LaunchedEffect(Unit) {
                if (!hasCamera) launcher.launch(Manifest.permission.CAMERA)
            }

            if (hasCamera) {
                cameraPreviewView { upc ->
                    setResult(RESULT_OK, Intent().putExtra("UPC", upc))
                    finish()
                }
            } else {
                Text("Camera permission required", Modifier.fillMaxSize())
            }
        }
    }
}