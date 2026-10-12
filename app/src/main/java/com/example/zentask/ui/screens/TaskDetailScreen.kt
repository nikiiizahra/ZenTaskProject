package com.example.zentask.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Info
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
fun TaskDetailScreen(
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
                            text = "Task Detail",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Surface(
                                color = ZenTaskTheme.PrimaryContainer,
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Text(
                                    text = "Development",
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    color = ZenTaskTheme.Primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Surface(
                                color = Color(0xFFFFE4E6),
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Text(
                                    text = "High Priority",
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    color = Color(0xFFE11D48),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "API Token Integration",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZenTaskTheme.TextPrimary
                        )

                        Text(
                            text = "Integrate authentication token into application API.",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = ZenTaskTheme.TextSecondary
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {

                        Text(
                            text = "Task Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZenTaskTheme.TextPrimary
                        )

                        DetailItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Category,
                                    contentDescription = "Category",
                                    tint = ZenTaskTheme.Primary
                                )
                            },
                            label = "Category",
                            value = "Development"
                        )

                        DetailItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Flag,
                                    contentDescription = "Priority",
                                    tint = Color(0xFFE11D48)
                                )
                            },
                            label = "Priority",
                            value = "High"
                        )

                        DetailItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = "Deadline",
                                    tint = Color(0xFF38BDF8)
                                )
                            },
                            label = "Deadline",
                            value = "12 October 2026"
                        )

                        DetailItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Status",
                                    tint = Color(0xFFF59E0B)
                                )
                            },
                            label = "Status",
                            value = "In Progress"
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE0F2FE)
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    Color(0xFF38BDF8),
                                    CircleShape
                                )
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Text(
                            text = "This task is currently in progress.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0369A1)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        navController.navigate("edit_task")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ZenTaskTheme.Primary
                    )
                ) {

                    Text(
                        text = "Edit Task",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
        }
    }
}

@Composable
fun DetailItem(
    icon: @Composable () -> Unit,
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF8FAFC)
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                icon()
            }
        }

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Column {

            Text(
                text = label,
                fontSize = 12.sp,
                color = ZenTaskTheme.TextSecondary
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ZenTaskTheme.TextPrimary
            )
        }
    }
}