package com.example.zentask.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.zentask.ZenTaskTheme
import com.example.zentask.ui.components.*
import com.example.zentask.viewmodel.ZenTaskViewModel

@Composable
fun ProfileScreen(navController: NavHostController, vm: ZenTaskViewModel) {
    val user by vm.userProfile.collectAsState()
    var pushOn by remember { mutableStateOf(user.pushEnabled) }
    var emailNotifOn by remember { mutableStateOf(user.emailNotifEnabled) }
    var showBillingDialog by remember { mutableStateOf(false) }

    ZenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                FloatingCapsuleNavBar(
                    currentRoute = "profile",
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Profile & Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)
                }
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(ZenTaskTheme.PrimaryLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AJ",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZenTaskTheme.Primary
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ZenTaskTheme.TextPrimary)
                                Text(user.email, fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = ZenTaskTheme.PrimaryContainer,
                                    shape = RoundedCornerShape(999.dp),
                                    modifier = Modifier.clickable { showBillingDialog = true }
                                ) {
                                    Text("Pro Member", modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp), color = ZenTaskTheme.Primary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Preferences", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZenTaskTheme.TextPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Push Notifications", fontSize = 13.sp, color = ZenTaskTheme.TextPrimary)
                            Switch(checked = pushOn, onCheckedChange = { pushOn = it })
                        }
                        HorizontalDivider(color = ZenTaskTheme.GlassBorder, modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Email Digest & Reminders", fontSize = 13.sp, color = ZenTaskTheme.TextPrimary)
                            Switch(checked = emailNotifOn, onCheckedChange = { emailNotifOn = it })
                        }
                    }
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Support & Help", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZenTaskTheme.TextPrimary)
                        Spacer(modifier = Modifier.height(10.dp))
                        TextButton(
                            onClick = { navController.navigate("help_center") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null, tint = ZenTaskTheme.TextPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Help Center & Feature Guides", color = ZenTaskTheme.TextPrimary)
                                }
                                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = ZenTaskTheme.TextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                        TextButton(
                            onClick = { showBillingDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = ZenTaskTheme.TextPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Subscription & Billing", color = ZenTaskTheme.TextPrimary)
                                }
                                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = ZenTaskTheme.TextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(8.dp, RoundedCornerShape(18.dp), ambientColor = Color(0x33EF4444))
                            .background(
                                Brush.horizontalGradient(listOf(Color(0xFFFEF2F2), Color(0xFFFFE4E6))),
                                RoundedCornerShape(18.dp)
                            )
                            .border(1.5.dp, Brush.linearGradient(listOf(Color(0xFFFCA5A5), Color(0xFFEF4444))), RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                navController.navigate("auth") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "Log Out", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Log Out", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), fontSize = 15.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }

        if (showBillingDialog) {
            ProBillingDialog(onDismiss = { showBillingDialog = false })
        }
    }
}
