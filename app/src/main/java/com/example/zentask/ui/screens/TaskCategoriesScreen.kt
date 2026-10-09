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
fun TaskCategoriesScreen(navController: NavHostController, vm: ZenTaskViewModel) {
    val categories by vm.categories.collectAsState()
    val tasks by vm.tasks.collectAsState()
    var showAddCategoryModal by remember { mutableStateOf(false) }

    ZenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                FloatingCapsuleNavBar(
                    currentRoute = "categories",
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Categories", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)
                        Text("Organize goals across workspaces", fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
                    }
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .shadow(6.dp, RoundedCornerShape(999.dp), ambientColor = Color(0x336E56CF))
                            .background(ZenTaskTheme.ButtonPrimaryGradient, RoundedCornerShape(999.dp))
                            .border(1.dp, ZenTaskTheme.GlassButtonBorderBrush, RoundedCornerShape(999.dp))
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { showAddCategoryModal = true }
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+ Add", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(categories) { cat ->
                        val catTasks = tasks.filter { it.categoryName == cat.name }
                        val totalTasks = catTasks.size.coerceAtLeast(cat.taskCount).coerceAtLeast(1)
                        val completedCount = catTasks.count { it.status == TaskStatus.COMPLETED }
                        val progressPct = (completedCount * 100) / totalTasks

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .shadow(6.dp, RoundedCornerShape(22.dp), spotColor = Color(0x1F6E56CF))
                                .border(1.dp, ZenTaskTheme.GlassBorder, RoundedCornerShape(22.dp)),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(cat.color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Folder,
                                            contentDescription = cat.name,
                                            tint = cat.color,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Box(modifier = Modifier.size(12.dp).background(cat.color, CircleShape))
                                }

                                Column {
                                    Text(cat.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZenTaskTheme.TextPrimary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${if (catTasks.isNotEmpty()) catTasks.size else cat.taskCount} Tasks • $progressPct% Done", fontSize = 11.sp, color = ZenTaskTheme.TextSecondary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { (progressPct / 100f).coerceIn(0f, 1f) },
                                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(999.dp)),
                                        color = cat.color,
                                        trackColor = cat.color.copy(alpha = 0.15f)
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(70.dp)) }
                }
            }
        }

        if (showAddCategoryModal) {
            AddCategoryBottomSheet(
                onDismiss = { showAddCategoryModal = false },
                onSave = { name, emoji, color ->
                    vm.addCategory(name, emoji, color)
                    showAddCategoryModal = false
                }
            )
        }
    }
}
