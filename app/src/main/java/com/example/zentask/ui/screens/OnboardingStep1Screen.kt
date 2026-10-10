package com.example.zentask.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zentask.ZenTaskTheme

@Composable
fun OnboardingStep1Screen(navController: NavController) {
    var selectedFocus by remember { mutableStateOf("Deep Study") }
    val goDashboard = { navController.navigate("dashboard") }

    Box(
        Modifier
            .fillMaxSize()
            .background(ZenTaskTheme.BackgroundBrush)
    ) {
        // Glow pink di sisi kanan
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            OnboardingTopBar(step = 1, chipText = "Step 1 of 3 • 1-Min Tour", onSkip = { goDashboard() })

            // Bagian tengah: bisa digulung kalau layar kecil
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(28.dp))

                // Kartu hero
                OnboardingGlassCard(Modifier.fillMaxWidth(), cornerRadius = 26.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(Modifier.size(68.dp), contentAlignment = Alignment.Center) {
                            Box(
                                Modifier
                                    .size(54.dp)
                                    .shadow(
                                        12.dp, CircleShape,
                                        ambientColor = ZenTaskTheme.Primary,
                                        spotColor = ZenTaskTheme.Primary
                                    )
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.AutoAwesome, null,
                                    tint = ZenTaskTheme.Primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text("👑", fontSize = 13.sp, modifier = Modifier.align(Alignment.TopEnd))
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            "WELCOME TO ZENTASK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.6.sp,
                            color = ZenTaskTheme.Primary,
                            modifier = Modifier
                                .background(ZenTaskTheme.PrimaryContainer, RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        )

                        Spacer(Modifier.height(14.dp))

                        Text(
                            "Focus Without Chaos.\nMindful Planning.",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ZenTaskTheme.TextPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 30.sp
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            "Experience a calm, distraction-free productivity space crafted to organize tasks with gentle clarity and zero cognitive clutter.",
                            fontSize = 13.sp,
                            color = ZenTaskTheme.TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("OUR CORE PHILOSOPHY", fontSize = 10.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp, color = ZenTaskTheme.TextSecondary)
                    Text("3 Mindful Pillars", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = ZenTaskTheme.Primary)
                }

                Spacer(Modifier.height(10.dp))

                PillarCard(
                    Icons.Outlined.Spa, ZenTaskTheme.StatusCompleted, ZenTaskTheme.StatusCompletedBg,
                    "Pure Intention", "One primary focus at a time to eliminate decision fatigue."
                )
                Spacer(Modifier.height(10.dp))
                PillarCard(
                    Icons.Outlined.Schedule, ZenTaskTheme.StatusInProgress, ZenTaskTheme.SecondaryContainer,
                    "Gentle Urgency", "Smart automatic vital filters surface what matters without panic."
                )

                Spacer(Modifier.height(14.dp))

                // Pilihan fokus
                OnboardingGlassCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Your Primary Focus Today:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = ZenTaskTheme.TextPrimary)
                            Text("Select one", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ZenTaskTheme.Primary)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FocusChip("Deep Study", Icons.Outlined.MenuBook, selectedFocus == "Deep Study", Modifier.weight(1f)) { selectedFocus = "Deep Study" }
                            FocusChip("Work & Projects", Icons.Outlined.WorkOutline, selectedFocus == "Work & Projects", Modifier.weight(1.35f)) { selectedFocus = "Work & Projects" }
                            FocusChip("Life Balance", Icons.Outlined.Spa, selectedFocus == "Life Balance", Modifier.weight(1.1f)) { selectedFocus = "Life Balance" }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
            }

            // Bagian bawah: tetap menempel di dasar layar
            OnboardingPageDots(selected = 1)
            Spacer(Modifier.height(14.dp))
            OnboardingPrimaryButton("Next: Mindful Urgency (Step 2)  →") {
                navController.navigate("onboarding/step2")
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Already familiar? Jump to Dashboard",
                fontSize = 12.sp,
                color = ZenTaskTheme.TextSecondary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { goDashboard() }
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Estimated time left: 60 seconds • ZenTask v2.4",
                fontSize = 10.sp,
                color = ZenTaskTheme.TextSubdued,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun PillarCard(icon: ImageVector, tint: Color, iconBg: Color, title: String, desc: String) {
    OnboardingGlassCard(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(40.dp).background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ZenTaskTheme.TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(desc, fontSize = 12.sp, color = ZenTaskTheme.TextSecondary, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun FocusChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (selected) ZenTaskTheme.Primary else Color.White, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            icon, null,
            tint = if (selected) Color.White else ZenTaskTheme.TextSecondary,
            modifier = Modifier.size(13.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            label,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            color = if (selected) Color.White else ZenTaskTheme.TextSecondary
        )
    }
}