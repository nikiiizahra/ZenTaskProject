package com.example.zentask.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zentask.ZenTaskTheme

@Composable
fun OnboardingStep2Screen(navController: NavController) {
    val goAuth = { navController.navigate("auth") }

    OnboardingBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            OnboardingTopBar(step = 2, chipText = "Step 2 of 3 • 1-Min Tour", onSkip = { goAuth() })

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(24.dp))

                // Kartu utama
                OnboardingGlassCard(Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AutoAwesome, null, tint = ZenTaskTheme.Primary, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "QUICK TOUR • STEP 2",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp,
                                color = ZenTaskTheme.Primary
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Mindful Task Management & Vital Urgency",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ZenTaskTheme.TextPrimary,
                            lineHeight = 27.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Organize your day with automated 48-hour urgent filters, custom color swatches, and collaborative task codes.",
                            fontSize = 12.sp,
                            color = ZenTaskTheme.TextSecondary,
                            lineHeight = 17.sp
                        )

                        Spacer(Modifier.height(14.dp))

                        // Contoh task urgent
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF5F6), RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier.size(32.dp).background(ZenTaskTheme.PriorityHighGlow, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.HourglassBottom, null, tint = ZenTaskTheme.PriorityHigh, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Finalize Client Pitch Deck", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ZenTaskTheme.TextPrimary)
                                Spacer(Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("14h left", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = ZenTaskTheme.PriorityHigh)
                                    Text("  •  ", fontSize = 10.sp, color = ZenTaskTheme.TextSubdued)
                                    Text("High", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = ZenTaskTheme.PriorityHigh)
                                }
                            }
                            Icon(Icons.Outlined.RadioButtonUnchecked, null, tint = ZenTaskTheme.TextSubdued, modifier = Modifier.size(22.dp))
                        }

                        Spacer(Modifier.height(10.dp))

                        // Dua mini kartu
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier.size(30.dp).background(ZenTaskTheme.PrimaryContainer, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Palette, null, tint = ZenTaskTheme.Primary, modifier = Modifier.size(16.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("UI Design", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ZenTaskTheme.TextPrimary)
                                    Text("12 Tasks", fontSize = 10.sp, color = ZenTaskTheme.TextSecondary)
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Flow Score", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ZenTaskTheme.TextPrimary)
                                    Text("Optimal", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = ZenTaskTheme.StatusCompleted)
                                }
                                FlowRing(progress = 0.78f, label = "78%")
                            }
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                FeatureCard(
                    Icons.Outlined.FilterAlt, ZenTaskTheme.PriorityHigh, ZenTaskTheme.PriorityHighGlow,
                    "Smart Vital Filter",
                    "Urgent tasks under 48 hours auto-highlighted for zero overwhelm."
                )
                Spacer(Modifier.height(10.dp))
                FeatureCard(
                    Icons.Outlined.CloudSync, ZenTaskTheme.StatusInProgress, ZenTaskTheme.SecondaryContainer,
                    "Seamless Sync & Collab",
                    "Share tasks and track progress with instant real-time cloud backup."
                )

                Spacer(Modifier.height(12.dp))
            }

            // Bagian bawah
            OnboardingPageDots(selected = 2)
            Spacer(Modifier.height(14.dp))
            OnboardingPrimaryButton("Next: Explore Categories  →") {
                navController.navigate("onboarding/step3")
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "← Back to Previous Step",
                    fontSize = 12.sp,
                    color = ZenTaskTheme.TextSecondary,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
                Spacer(Modifier.width(24.dp))
                Text(
                    "Jump to Dashboard",
                    fontSize = 12.sp,
                    color = ZenTaskTheme.TextSecondary,
                    modifier = Modifier.clickable { goAuth() }
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Estimated time left: 40 seconds • ZenTask v2.4",
                fontSize = 10.sp,
                color = ZenTaskTheme.TextSubdued,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun FeatureCard(icon: ImageVector, tint: Color, iconBg: Color, title: String, desc: String) {
    OnboardingGlassCard(Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(40.dp).background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ZenTaskTheme.TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(desc, fontSize = 11.sp, color = ZenTaskTheme.TextSecondary, lineHeight = 15.sp)
            }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Outlined.CheckCircle, null, tint = ZenTaskTheme.TextSubdued, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun FlowRing(progress: Float, label: String) {
    Box(Modifier.size(38.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = 4.dp.toPx()
            val arcSize = Size(size.width - w, size.height - w)
            val topLeft = Offset(w / 2, w / 2)
            drawArc(
                color = ZenTaskTheme.StatusCompletedBg,
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(w)
            )
            drawArc(
                color = ZenTaskTheme.StatusCompleted,
                startAngle = -90f, sweepAngle = 360f * progress, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(w, cap = StrokeCap.Round)
            )
        }
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)
    }
}