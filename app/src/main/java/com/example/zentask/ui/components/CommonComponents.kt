package com.example.zentask.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zentask.ZenTaskTheme

@Composable
fun ZenBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ZenTaskTheme.BackgroundBrush)
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset((-40).dp, (-30).dp)
                .background(Color(0x33E1D9FF), CircleShape)
                .blur(40.dp)
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomEnd)
                .offset(40.dp, 30.dp)
                .background(Color(0x28FED7E2), CircleShape)
                .blur(40.dp)
        )
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderStrokeWidth: Dp = 1.dp,
    borderColor: Color = ZenTaskTheme.GlassBorder,
    backgroundColor: Color = ZenTaskTheme.GlassSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = ZenTaskTheme.GlassCardElevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color(0x1A6E56CF),
                spotColor = Color(0x1F6E56CF)
            )
            .border(borderStrokeWidth, borderColor, RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@Composable
fun ZenPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(999.dp),
                ambientColor = Color(0x446E56CF),
                spotColor = Color(0x666E56CF)
            )
            .background(ZenTaskTheme.ButtonPrimaryGradient, RoundedCornerShape(999.dp))
            .border(1.5.dp, ZenTaskTheme.GlassButtonBorderBrush, RoundedCornerShape(999.dp))
            .clip(RoundedCornerShape(999.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            if (icon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ZenSecondaryGlassButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(999.dp),
                ambientColor = Color(0x1F6E56CF),
                spotColor = Color(0x266E56CF)
            )
            .background(ZenTaskTheme.SecondaryGlassGradient, RoundedCornerShape(999.dp))
            .border(1.5.dp, ZenTaskTheme.GlassButtonBorderBrush, RoundedCornerShape(999.dp))
            .clip(RoundedCornerShape(999.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ZenTaskTheme.Primary)
            if (icon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(icon, contentDescription = null, tint = ZenTaskTheme.Primary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun FloatingCapsuleNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 18.dp)
            .height(68.dp)
            .shadow(18.dp, RoundedCornerShape(999.dp), ambientColor = Color(0x266E56CF), spotColor = Color(0x266E56CF))
            .border(1.dp, ZenTaskTheme.GlassBorder, RoundedCornerShape(999.dp)),
        shape = RoundedCornerShape(999.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xEEFFFFFF))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val items = listOf(
                Triple("dashboard", Icons.Default.Home, "Home"),
                Triple("tasks", Icons.Default.CheckCircle, "Tasks"),
                Triple("vital", Icons.Default.Star, "Vital"),
                Triple("categories", Icons.Default.Folder, "Categories"),
                Triple("profile", Icons.Default.Person, "Profile")
            )

            items.forEach { (route, icon, label) ->
                val isSelected = currentRoute == route
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) ZenTaskTheme.PrimaryLight.copy(alpha = 0.7f) else Color.Transparent)
                        .clickable { onNavigate(route) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) ZenTaskTheme.Primary else ZenTaskTheme.TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PhilosophyPillarItem(
    icon: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(72.dp)
                .height(44.dp)
                .background(
                    color = ZenTaskTheme.PrimaryContainer,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ZenTaskTheme.Primary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ZenTaskTheme.TextPrimary
            )

            Text(
                text = desc,
                fontSize = 12.sp,
                color = ZenTaskTheme.TextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun ReadyCheckRow(title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(ZenTaskTheme.StatusCompletedBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = ZenTaskTheme.StatusCompleted, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ZenTaskTheme.TextPrimary)
            Text(subtitle, fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
        }
    }
}

@Composable
fun ConfettiCanvasOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val colors = listOf(Color(0xFF6E56CF), Color(0xFFF43F5E), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF38BDF8))
        for (i in 0..40) {
            val x = (i * 27) % size.width
            val y = ((i * 47) % size.height)
            drawCircle(
                color = colors[i % colors.size],
                radius = (i % 6 + 4).toFloat(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun SocialLoginPill(label: String, icon: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, ZenTaskTheme.GlassBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ZenTaskTheme.TextPrimary)
        }
    }
}
