package com.example.zentask.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.zentask.ZenTaskTheme
import com.example.zentask.ui.components.ZenBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavHostController
) {
    ZenBackground {

        Scaffold(
            containerColor = Color.Transparent,

            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),

                    title = {
                        Text(
                            text = "Notifications",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ZenTaskTheme.TextPrimary
                        )
                    },

                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = ZenTaskTheme.TextPrimary
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Today",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenTaskTheme.TextPrimary
                )

                NotificationItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = "Deadline",
                            tint = Color(0xFFF59E0B)
                        )
                    },
                    title = "Task deadline approaching",
                    message = "API Token Integration is due tomorrow at 10:00 AM.",
                    time = "10 min ago",
                    iconBackground = Color(0xFFFEF3C7)
                )

                NotificationItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Group,
                            contentDescription = "Collaboration",
                            tint = ZenTaskTheme.Primary
                        )
                    },
                    title = "New collaboration",
                    message = "A teammate joined your shared task.",
                    time = "1 hour ago",
                    iconBackground = ZenTaskTheme.PrimaryContainer
                )

                NotificationItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Completed",
                            tint = Color(0xFF10B981)
                        )
                    },
                    title = "Task completed",
                    message = "Cognitive Psychology Chapter 4 has been completed.",
                    time = "Yesterday",
                    iconBackground = Color(0xFFD1FAE5)
                )
            }
        }
    }
}

@Composable
fun NotificationItem(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
    time: String,
    iconBackground: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = iconBackground,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = message,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFF475569)
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = time,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}