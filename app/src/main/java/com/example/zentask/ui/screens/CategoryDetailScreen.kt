package com.example.zentask.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Folder
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
fun CategoryDetailScreen(
    navController: NavHostController,
    categoryName: String
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
                            text = categoryName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
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
                                tint = Color(0xFF0F172A)
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

                // CATEGORY SUMMARY
                Surface(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(22.dp),

                    color = Color.White,

                    shadowElevation = 2.dp
                ) {

                    Row(
                        modifier = Modifier.padding(18.dp),

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    ZenTaskTheme.PrimaryContainer,
                                    RoundedCornerShape(15.dp)
                                ),

                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.Folder,
                                contentDescription = "Category",
                                tint = ZenTaskTheme.Primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(14.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = categoryName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "3 Tasks • 1 Completed",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF475569)
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            LinearProgressIndicator(
                                progress = { 0.33f },

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),

                                color = ZenTaskTheme.Primary,

                                trackColor = ZenTaskTheme.PrimaryContainer
                            )
                        }
                    }
                }

                Text(
                    text = "Tasks in $categoryName",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                CategoryTaskCard(
                    title = "Finalize Design System Components",
                    description = "Complete remaining UI components.",
                    status = "In Progress",
                    deadline = "Today, 5:00 PM",
                    statusColor = Color(0xFF0284C7),
                    statusBackground = Color(0xFFE0F2FE),

                    onClick = {
                        navController.navigate("task_detail")
                    }
                )

                CategoryTaskCard(
                    title = "API Token Integration",
                    description = "Connect authentication token with API.",
                    status = "Not Started",
                    deadline = "Tomorrow, 10:00 AM",
                    statusColor = Color(0xFF64748B),
                    statusBackground = Color(0xFFF1F5F9),

                    onClick = {
                        navController.navigate("task_detail")
                    }
                )

                CategoryTaskCard(
                    title = "Cognitive Psychology Chapter 4",
                    description = "Summary and spaced repetition cards completed.",
                    status = "Completed",
                    deadline = "Finished Yesterday",
                    statusColor = Color(0xFF059669),
                    statusBackground = Color(0xFFD1FAE5),

                    onClick = {
                        navController.navigate("task_detail")
                    }
                )
            }
        }
    }
}


@Composable
fun CategoryTaskCard(
    title: String,
    description: String,
    status: String,
    deadline: String,
    statusColor: Color,
    statusBackground: Color,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(20.dp),

        color = Color.White,

        shadowElevation = 2.dp
    ) {

        Column(
            modifier = Modifier.padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = title,

                    modifier = Modifier.weight(1f),

                    fontSize = 15.sp,

                    fontWeight = FontWeight.Bold,

                    color = Color(0xFF0F172A)
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Surface(
                    color = statusBackground,

                    shape = RoundedCornerShape(999.dp)
                ) {

                    Text(
                        text = status,

                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 5.dp
                        ),

                        fontSize = 11.sp,

                        fontWeight = FontWeight.Bold,

                        color = statusColor
                    )
                }
            }

            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = Color(0xFF475569)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = "Deadline",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(15.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = deadline,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}