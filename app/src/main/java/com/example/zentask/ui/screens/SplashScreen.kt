package com.example.zentask.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zentask.R
import com.example.zentask.ZenTaskTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset

@Composable
fun SplashScreen(navController: NavController) {
    val iconProgress = remember { Animatable(0f) }  // 0 = pojok kiri atas, 1 = tengah
    val iconAlpha = remember { Animatable(0f) }
    val textShift = remember { Animatable(1f) }     // 1 = di luar layar kanan, 0 = tengah
    val textAlpha = remember { Animatable(0f) }
    val boomScale = remember { Animatable(0f) }
    val boomCorner = remember { Animatable(50f) }   // 50 = pil, 0 = persegi penuh

    LaunchedEffect(Unit) {
        launch { iconAlpha.animateTo(1f, tween(400)) }
        iconProgress.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        launch { textAlpha.animateTo(1f, tween(500)) }
        textShift.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
        delay(800)

        // BOOM: kotak ungu membesar sampai memenuhi layar
        launch { boomCorner.animateTo(0f, tween(700, easing = FastOutSlowInEasing)) }
        boomScale.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
        delay(300)

        navController.navigate("onboarding/step1") {
            popUpTo("splash") { inclusive = true }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(ZenTaskTheme.BackgroundBrush)
    ) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val heightPx = with(LocalDensity.current) { maxHeight.toPx() }

        // Lingkaran blur dekoratif (kiri atas & bawah)
        Box(
            Modifier
                .align(Alignment.TopStart)
                .offset(x = (-30).dp, y = 20.dp)
                .size(160.dp)
                .background(
                    Brush.radialGradient(listOf(ZenTaskTheme.PrimaryLight.copy(alpha = 0.7f), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-80).dp)
                .size(200.dp)
                .background(
                    Brush.radialGradient(listOf(ZenTaskTheme.SecondaryContainer.copy(alpha = 0.7f), Color.Transparent)),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ikon: meluncur dari pojok kiri atas ke tengah
            Image(
                painter = painterResource(R.drawable.ic_splash_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        val p = iconProgress.value
                        translationX = -(1f - p) * widthPx / 2f
                        translationY = -(1f - p) * heightPx / 2f
                        alpha = iconAlpha.value
                    }
            )

            Spacer(Modifier.height(16.dp))

            // Teks: masuk dari kanan ke tengah
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    translationX = textShift.value * widthPx
                    alpha = textAlpha.value
                }
            ) {
                Text(
                    "ZenTask",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTaskTheme.TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Focus. Plan. Achieve with Mindful Ease.",
                    fontSize = 18.sp,
                    color = ZenTaskTheme.TextSecondary
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(50), ambientColor = ZenTaskTheme.Primary.copy(alpha = 0.2f))
                        .background(Color.White, RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .background(ZenTaskTheme.StatusCompleted, CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Task Manager ✨",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ZenTaskTheme.Primary
                    )
                }
            }
        }
        if (boomScale.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = boomScale.value
                        scaleY = boomScale.value
                        shape = RoundedCornerShape(percent = boomCorner.value.toInt())
                        clip = true
                    }
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF7C62D6), Color(0xFFA48FE8), Color(0xFFD3CBF9)),
                            start = Offset(0f, heightPx),
                            end = Offset(widthPx, 0f)
                        )
                    )
            ) {
                // Glow putih kiri atas
                Box(
                    Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 20.dp, y = 30.dp)
                        .size(140.dp)
                        .background(
                            Brush.radialGradient(listOf(Color.White.copy(alpha = 0.45f), Color.Transparent)),
                            CircleShape
                        )
                )
                // Glow putih bawah
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-90).dp)
                        .size(180.dp)
                        .background(
                            Brush.radialGradient(listOf(Color.White.copy(alpha = 0.5f), Color.Transparent)),
                            CircleShape
                        )
                )
            }
        }
    }
}