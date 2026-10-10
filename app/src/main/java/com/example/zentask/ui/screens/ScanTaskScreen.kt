package com.example.zentask.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlipCameraAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.zentask.ZenTaskTheme
import java.io.File
import java.util.concurrent.Executors

@Composable
fun ScanTaskScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var scannedPdf by remember { mutableStateOf<File?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasPermission = it }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            isProcessing = true
            cameraExecutor.execute {
                val pdf = copyUriToCache(context, uri)?.let { imageToPdf(context, it) }
                mainExecutor.execute { scannedPdf = pdf; isProcessing = false }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(hasPermission, lensFacing) {
        if (!hasPermission) return@LaunchedEffect
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            val provider = future.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val selector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            try {
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, mainExecutor)
    }

    fun capture() {
        if (isProcessing || !hasPermission) return
        isProcessing = true
        val photo = File(context.cacheDir, "cap_${System.currentTimeMillis()}.jpg")
        imageCapture.takePicture(
            ImageCapture.OutputFileOptions.Builder(photo).build(),
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val pdf = imageToPdf(context, photo)
                    mainExecutor.execute { scannedPdf = pdf; isProcessing = false }
                }
                override fun onError(exc: ImageCaptureException) {
                    mainExecutor.execute { isProcessing = false }
                }
            }
        )
    }

    OnboardingBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, null, tint = ZenTaskTheme.Primary, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Scan Task", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = ZenTaskTheme.TextPrimary)
                    Text("Turn a page into your next step.", fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Pill "Document"
            Box(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.62f)
                    .background(ZenTaskTheme.Primary, RoundedCornerShape(50))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Document", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(14.dp))

            // Kamera
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1E1B2E))
            ) {
                if (hasPermission) {
                    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
                } else {
                    Text(
                        "Izinkan akses kamera untuk mulai scan",
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable { permissionLauncher.launch(Manifest.permission.CAMERA) }
                    )
                }

                ScanOverlay()

                Row(
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(6.dp).background(ZenTaskTheme.Primary, CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Text("Document detected", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = ZenTaskTheme.TextPrimary)
                }

                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(30.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.FlashOff, null, tint = ZenTaskTheme.TextSecondary, modifier = Modifier.size(16.dp))
                }

                Text(
                    "Keep the page inside the frame",
                    fontSize = 11.sp,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            // Kontrol
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Gallery",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZenTaskTheme.Primary,
                        modifier = Modifier
                            .shadow(3.dp, RoundedCornerShape(50))
                            .background(Color.White, RoundedCornerShape(50))
                            .clickable {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .padding(horizontal = 22.dp, vertical = 10.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Upload photo", fontSize = 10.sp, color = ZenTaskTheme.TextSecondary)
                }

                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .size(66.dp)
                            .border(2.dp, ZenTaskTheme.Primary, CircleShape)
                            .padding(5.dp)
                            .background(ZenTaskTheme.Primary, CircleShape)
                            .clickable { capture() }
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (isProcessing) "Saving..." else "Capture",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZenTaskTheme.Primary
                    )
                }

                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape)
                            .clickable {
                                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                                    CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.FlipCameraAndroid, null, tint = ZenTaskTheme.Primary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Flip camera", fontSize = 10.sp, color = ZenTaskTheme.TextSecondary)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Kartu "Use your scan"
            OnboardingGlassCard(Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                Column(Modifier.padding(16.dp)) {
                    Box(
                        Modifier
                            .width(28.dp)
                            .height(4.dp)
                            .background(ZenTaskTheme.PrimaryLight, RoundedCornerShape(50))
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("Use your scan", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = ZenTaskTheme.TextPrimary)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        if (scannedPdf != null) "1 page captured · Saved as PDF" else "No page captured yet",
                        fontSize = 12.sp,
                        color = ZenTaskTheme.TextSecondary
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(Modifier.alpha(if (scannedPdf != null) 1f else 0.5f)) {
                        OnboardingPrimaryButton("Create Task from Scan  →") {
                            val pdf = scannedPdf ?: return@OnboardingPrimaryButton
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("scanned_pdf_path", pdf.absolutePath)
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanOverlay() {
    val transition = rememberInfiniteTransition(label = "scan")
    val lineY by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.78f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Reverse),
        label = "lineY"
    )
    val bracket = Color(0xFFD3CBF9)

    Canvas(Modifier.fillMaxSize()) {
        val l = size.width * 0.16f
        val r = size.width * 0.84f
        val t = size.height * 0.14f
        val b = size.height * 0.84f
        val len = 34.dp.toPx()
        val sw = 3.dp.toPx()

        fun corner(x: Float, y: Float, dx: Float, dy: Float) {
            drawLine(bracket, Offset(x, y), Offset(x + dx * len, y), sw, StrokeCap.Round)
            drawLine(bracket, Offset(x, y), Offset(x, y + dy * len), sw, StrokeCap.Round)
        }
        corner(l, t, 1f, 1f)
        corner(r, t, -1f, 1f)
        corner(l, b, 1f, -1f)
        corner(r, b, -1f, -1f)

        val y = size.height * lineY
        drawLine(Color(0xFF9E7AFF), Offset(l + 8.dp.toPx(), y), Offset(r - 8.dp.toPx(), y), 2.dp.toPx(), StrokeCap.Round)
    }
}