package com.example.zentask.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.zentask.ZenTaskTheme
import com.example.zentask.model.TaskStatus
import com.example.zentask.ui.components.*
import com.example.zentask.viewmodel.ZenTaskViewModel

@Composable
fun TaskCategoriesScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel
) {
    val categories by vm.categories.collectAsState()
    val tasks by vm.tasks.collectAsState()

    var showAddCategoryModal by remember {
        mutableStateOf(false)
    }

    ZenBackground {

        Scaffold(
            containerColor = Color.Transparent,

            bottomBar = {
                FloatingCapsuleNavBar(
                    currentRoute = "categories",
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Categories",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = "Organize goals across workspaces",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(999.dp),
                                ambientColor = Color(0x336E56CF)
                            )
                            .background(
                                brush = ZenTaskTheme.ButtonPrimaryGradient,
                                shape = RoundedCornerShape(999.dp)
                            )
                            .border(
                                width = 1.dp,
                                brush = ZenTaskTheme.GlassButtonBorderBrush,
                                shape = RoundedCornerShape(999.dp)
                            )
                            .clip(
                                RoundedCornerShape(999.dp)
                            )
                            .clickable {
                                showAddCategoryModal = true
                            }
                            .padding(horizontal = 18.dp),

                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "+ Add",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(14.dp),

                    modifier = Modifier.fillMaxSize()
                ) {

                    items(categories) { cat ->

                        val catTasks = tasks.filter {
                            it.categoryName == cat.name
                        }

                        val totalTasks = catTasks.size
                            .coerceAtLeast(cat.taskCount)
                            .coerceAtLeast(1)

                        val completedCount = catTasks.count {
                            it.status == TaskStatus.COMPLETED
                        }

                        val progressPct =
                            (completedCount * 100) / totalTasks

                        val shownTaskCount =
                            if (catTasks.isNotEmpty()) {
                                catTasks.size
                            } else {
                                cat.taskCount
                            }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(155.dp)
                                .shadow(
                                    elevation = 5.dp,
                                    shape = RoundedCornerShape(22.dp),
                                    spotColor = Color(0x1F6E56CF)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(22.dp)
                                )
                                .clickable {
                                    navController.navigate(
                                        "category_detail/${cat.name}"
                                    )
                                },

                            shape = RoundedCornerShape(22.dp),

                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),

                                verticalArrangement =
                                    Arrangement.SpaceBetween
                            ) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),

                                    horizontalArrangement =
                                        Arrangement.SpaceBetween,

                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(
                                                color = cat.color.copy(
                                                    alpha = 0.15f
                                                ),
                                                shape = RoundedCornerShape(
                                                    13.dp
                                                )
                                            ),

                                        contentAlignment =
                                            Alignment.Center
                                    ) {

                                        Icon(
                                            imageVector =
                                                Icons.Outlined.Folder,

                                            contentDescription =
                                                cat.name,

                                            tint = cat.color,

                                            modifier =
                                                Modifier.size(22.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(11.dp)
                                            .background(
                                                color = cat.color,
                                                shape = CircleShape
                                            )
                                    )
                                }

                                Column {

                                    Text(
                                        text = cat.name,

                                        fontWeight =
                                            FontWeight.Bold,

                                        fontSize = 15.sp,

                                        lineHeight = 19.sp,

                                        color = Color(0xFF0F172A)
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(4.dp)
                                    )

                                    Text(
                                        text =
                                            "$shownTaskCount Tasks • $progressPct% Done",

                                        fontSize = 12.sp,

                                        fontWeight =
                                            FontWeight.Medium,

                                        color = Color(0xFF475569)
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(8.dp)
                                    )

                                    LinearProgressIndicator(
                                        progress = {
                                            (progressPct / 100f)
                                                .coerceIn(0f, 1f)
                                        },

                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(
                                                RoundedCornerShape(
                                                    999.dp
                                                )
                                            ),

                                        color = cat.color,

                                        trackColor =
                                            cat.color.copy(
                                                alpha = 0.16f
                                            )
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(
                            modifier = Modifier.height(80.dp)
                        )
                    }
                }
            }
        }

        if (showAddCategoryModal) {

            AddCategoryBottomSheet(
                onDismiss = {
                    showAddCategoryModal = false
                },

                onSave = { name, emoji, color ->

                    vm.addCategory(
                        name,
                        emoji,
                        color
                    )

                    showAddCategoryModal = false
                }
            )
        }
    }
}