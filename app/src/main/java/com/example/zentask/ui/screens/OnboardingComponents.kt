package com.example.zentask.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentask.ZenTaskTheme
import androidx.compose.ui.graphics.Brush

@Composable
fun OnboardingBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(ZenTaskTheme.BackgroundBrush)
    ) {
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 90.dp)
                .size(260.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0xFFFFD6E8).copy(alpha = 0.6f), Color.Transparent)),
                    CircleShape
                )
        )
        content()
    }
}

@Composable
fun OnboardingGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .shadow(
                ZenTaskTheme.GlassCardElevation, shape,
                ambientColor = ZenTaskTheme.Primary.copy(alpha = 0.15f),
                spotColor = ZenTaskTheme.Primary.copy(alpha = 0.15f)
            )
            .background(ZenTaskTheme.GlassSurface, shape)
            .border(1.dp, ZenTaskTheme.GlassBorder, shape)
    ) { content() }
}

@Composable
fun OnboardingTopBar(
    step: Int,
    chipText: String,
    dotColor: Color = ZenTaskTheme.Primary,
    onSkip: () -> Unit
) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { i ->
                val color = when {
                    i + 1 == step -> ZenTaskTheme.Primary
                    i + 1 < step -> ZenTaskTheme.Primary.copy(alpha = 0.6f)
                    else -> ZenTaskTheme.Primary.copy(alpha = 0.15f)
                }
                Box(
                    Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(color, RoundedCornerShape(50))
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(Modifier.size(7.dp).background(dotColor, CircleShape))
                Spacer(Modifier.width(8.dp))
                Text(
                    chipText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ZenTaskTheme.TextPrimary
                )
            }
            Text(
                "Skip Tour ›",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ZenTaskTheme.TextSecondary,
                modifier = Modifier.clickable(onClick = onSkip)
            )
        }
    }
}

@Composable
fun OnboardingPageDots(selected: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { i ->
            val active = i + 1 == selected
            Box(
                Modifier
                    .padding(horizontal = 3.dp)
                    .height(6.dp)
                    .width(if (active) 20.dp else 6.dp)
                    .background(
                        if (active) ZenTaskTheme.Primary else ZenTaskTheme.Primary.copy(alpha = 0.2f),
                        CircleShape
                    )
            )
        }
    }
}

@Composable
fun OnboardingPrimaryButton(text: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(
                10.dp, shape,
                ambientColor = ZenTaskTheme.Primary,
                spotColor = ZenTaskTheme.Primary
            )
            .clip(shape)
            .background(ZenTaskTheme.ButtonPrimaryGradient, shape)
            .border(1.dp, ZenTaskTheme.GlassButtonBorderBrush, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}