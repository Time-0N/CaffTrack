package ch.timeon.cafftrack.ui

import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.camera.view.PreviewView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.barcode.BarcodeScanning

@Composable
fun cameraPreviewView(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onBarcodeDetected: (String) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                ProcessCameraProvider.getInstance(ctx).addListener({
                    val cameraProvider = ProcessCameraProvider.getInstance(ctx).get()
                    val previewUseCase = Preview.Builder().build().also {
                        it.setSurfaceProvider(surfaceProvider)
                    }
                    val analysisUseCase = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build().also { analysis ->
                            val scanner = BarcodeScanning.getClient()
                            analysis.setAnalyzer(
                                ContextCompat.getMainExecutor(ctx)
                            ) { imageProxy ->
                                imageProxy.image?.let { mediaImg ->
                                    val inputImg = InputImage.fromMediaImage(
                                        mediaImg, imageProxy.imageInfo.rotationDegrees
                                    )
                                    scanner.process(inputImg)
                                        .addOnSuccessListener { barcodes ->
                                            barcodes.firstOrNull()?.rawValue?.let(onBarcodeDetected)
                                        }
                                        .addOnCompleteListener { imageProxy.close() }
                                } ?: imageProxy.close()
                            }
                        }
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        previewUseCase,
                        analysisUseCase
                    )
                }, ContextCompat.getMainExecutor(ctx))
            }
        }
    )
}