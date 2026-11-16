
package com.example.zentask.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
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
import com.example.zentask.viewmodel.DemoAuthStore
import com.example.zentask.viewmodel.ZenTaskViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel
) {
    val user by DemoAuthStore.currentUser.collectAsState()

    var showBillingDialog by remember {
        mutableStateOf(false)
    }

    ZenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                FloatingCapsuleNavBar(
                    currentRoute = "profile",
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement =
                    Arrangement.spacedBy(16.dp)
            ) {

                // HEADER
                item {
                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Profile & Settings",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Manage your account and preferences",
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )
                }

                // USER PROFILE
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(
                                        ZenTaskTheme.PrimaryLight,
                                        CircleShape
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {
                                Text(
                                    text = user?.initials ?: "?",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ZenTaskTheme.Primary
                                )
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = user?.fullName
                                        ?: "Not signed in",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF0F172A)
                                )

                                Spacer(Modifier.height(3.dp))

                                Text(
                                    text = user?.email
                                        ?: "No active demo account",
                                    fontSize = 13.sp,
                                    color = Color(0xFF475569)
                                )

                                Spacer(Modifier.height(8.dp))

                                Surface(
                                    color =
                                        ZenTaskTheme.PrimaryContainer,
                                    shape =
                                        RoundedCornerShape(999.dp),
                                    modifier = Modifier.clickable {
                                        showBillingDialog = true
                                    }
                                ) {
                                    Text(
                                        text = "Local Account",
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 5.dp
                                        ),
                                        color = ZenTaskTheme.Primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // PREFERENCES
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Preferences",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Push Notifications",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF0F172A)
                                )

                                Text(
                                    text = "Receive reminders for important tasks",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Switch(
                                checked =
                                    user?.pushEnabled ?: false,
                                enabled = user != null,
                                onCheckedChange = {
                                    DemoAuthStore
                                        .updatePushNotifications(it)
                                }
                            )
                        }

                        HorizontalDivider(
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(
                                vertical = 10.dp
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Email Digest & Reminders",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF0F172A)
                                )

                                Text(
                                    text = "Get periodic task summaries by email",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Switch(
                                checked =
                                    user?.emailNotifEnabled
                                        ?: false,
                                enabled = user != null,
                                onCheckedChange = {
                                    DemoAuthStore
                                        .updateEmailNotifications(it)
                                }
                            )
                        }

                        Text(
                            text = "Notification preferences are saved on this device. The delivery of automated push notifications and emails has not yet been enabled.",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(
                                top = 12.dp
                            )
                        )
                    }
                }

                // SUPPORT & HELP
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Support & Help",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(Modifier.height(10.dp))

                        TextButton(
                            onClick = {
                                navController.navigate(
                                    "help_center"
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.SpaceBetween,
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector =
                                            Icons.AutoMirrored
                                                .Outlined
                                                .HelpOutline,
                                        contentDescription = null,
                                        tint =
                                            ZenTaskTheme.Primary,
                                        modifier =
                                            Modifier.size(21.dp)
                                    )

                                    Spacer(Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = "Help Center & Feature Guides",
                                            fontSize = 14.sp,
                                            fontWeight =
                                                FontWeight.Medium,
                                            color =
                                                Color(0xFF0F172A)
                                        )

                                        Text(
                                            text = "FAQ and app usage guides",
                                            fontSize = 12.sp,
                                            color =
                                                Color(0xFF64748B)
                                        )
                                    }
                                }

                                Icon(
                                    imageVector =
                                        Icons.Outlined.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        HorizontalDivider(
                            color = Color(0xFFE2E8F0)
                        )

                        TextButton(
                            onClick = {
                                showBillingDialog = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.SpaceBetween,
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector =
                                            Icons.Outlined.CreditCard,
                                        contentDescription = null,
                                        tint =
                                            ZenTaskTheme.Primary,
                                        modifier =
                                            Modifier.size(21.dp)
                                    )

                                    Spacer(Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = "Subscription & Billing",
                                            fontSize = 14.sp,
                                            fontWeight =
                                                FontWeight.Medium,
                                            color =
                                                Color(0xFF0F172A)
                                        )

                                        Text(
                                            text = "Manage your current plan",
                                            fontSize = 12.sp,
                                            color =
                                                Color(0xFF64748B)
                                        )
                                    }
                                }

                                Icon(
                                    imageVector =
                                        Icons.Outlined.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // LOG OUT
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(
                                8.dp,
                                RoundedCornerShape(18.dp),
                                ambientColor =
                                    Color(0x33EF4444)
                            )
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFFEF2F2),
                                        Color(0xFFFFE4E6)
                                    )
                                ),
                                RoundedCornerShape(18.dp)
                            )
                            .border(
                                1.5.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFCA5A5),
                                        Color(0xFFEF4444)
                                    )
                                ),
                                RoundedCornerShape(18.dp)
                            )
                            .clip(
                                RoundedCornerShape(18.dp)
                            )
                            .clickable {

                                // Hapus sesi akun demo.
                                DemoAuthStore.logout()

                                // Kembali ke Login,
                                // tanpa membuka halaman lama
                                // melalui tombol Back.
                                navController.navigate("auth") {
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector =
                                    Icons.AutoMirrored
                                        .Outlined
                                        .ExitToApp,
                                contentDescription = "Log Out",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(19.dp)
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = "Log Out",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626),
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(80.dp))
                }
            }
        }

        if (showBillingDialog) {
            ProBillingDialog(
                onDismiss = {
                    showBillingDialog = false
                }
            )
        }
    }
}
