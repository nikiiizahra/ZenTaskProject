package com.example.zentask.ui.screens

import android.net.Uri

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavHostController

import com.example.zentask.ZenTaskTheme
import com.example.zentask.model.TaskStatus
import com.example.zentask.ui.components.*
import com.example.zentask.viewmodel.ZenTaskViewModel

// =====================================================
// TASK CATEGORIES SCREEN
// =====================================================

@Composable
fun TaskCategoriesScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel
) {

    // Categories and tasks are observed from
    // the active user's Room-backed ViewModel.
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

                // =====================================
                // HEADER
                // =====================================

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

                    // =================================
                    // ADD CATEGORY BUTTON
                    // =================================

                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape =
                                    RoundedCornerShape(999.dp),
                                ambientColor =
                                    Color(0x336E56CF)
                            )
                            .background(
                                brush =
                                    ZenTaskTheme.ButtonPrimaryGradient,
                                shape =
                                    RoundedCornerShape(999.dp)
                            )
                            .border(
                                width = 1.dp,
                                brush =
                                    ZenTaskTheme.GlassButtonBorderBrush,
                                shape =
                                    RoundedCornerShape(999.dp)
                            )
                            .clip(
                                RoundedCornerShape(999.dp)
                            )
                            .clickable {
                                showAddCategoryModal = true
                            }
                            .padding(horizontal = 18.dp),

                        contentAlignment =
                            Alignment.Center
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

                // =====================================
                // CATEGORY GRID
                // =====================================

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(14.dp),

                    modifier = Modifier.fillMaxSize()
                ) {

                    if (categories.isEmpty()) {

                        item(
                            span = {
                                androidx.compose.foundation.lazy.grid
                                    .GridItemSpan(maxLineSpan)
                            }
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 50.dp),

                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "No categories yet",
                                    fontWeight =
                                        FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color(0xFF0F172A)
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(8.dp)
                                )

                                Text(
                                    text = "Tap + Add to create your first category.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                    } else {

                        items(
                            items = categories,
                            key = { category ->
                                category.id
                            }
                        ) { cat ->

                            // Only tasks of this category.
                            val catTasks = tasks.filter {
                                    task ->

                                task.categoryName == cat.name
                            }

                            val totalTasks = catTasks.size

                            val completedCount =
                                catTasks.count { task ->
                                    task.status ==
                                            TaskStatus.COMPLETED
                                }

                            // No tasks means 0%, not 100%.
                            val progressPct =
                                if (totalTasks > 0) {
                                    (
                                            completedCount * 100
                                            ) / totalTasks
                                } else {
                                    0
                                }

                            // Safely encode names containing
                            // slash, spaces, or other symbols.
                            val encodedCategoryName =
                                Uri.encode(cat.name)

                            // =================================
                            // CATEGORY CARD
                            // =================================

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(155.dp)
                                    .shadow(
                                        elevation = 5.dp,
                                        shape =
                                            RoundedCornerShape(22.dp),
                                        spotColor =
                                            Color(0x1F6E56CF)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color =
                                            Color(0xFFE2E8F0),
                                        shape =
                                            RoundedCornerShape(22.dp)
                                    )
                                    .clickable {

                                        navController.navigate(
                                            "category_detail/$encodedCategoryName"
                                        )
                                    },

                                shape =
                                    RoundedCornerShape(22.dp),

                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            Color.White
                                    )
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),

                                    verticalArrangement =
                                        Arrangement.SpaceBetween
                                ) {

                                    // =================================
                                    // ICON AND COLOR INDICATOR
                                    // =================================

                                    Row(
                                        modifier =
                                            Modifier.fillMaxWidth(),

                                        horizontalArrangement =
                                            Arrangement.SpaceBetween,

                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(
                                                    color =
                                                        cat.color.copy(
                                                            alpha = 0.15f
                                                        ),

                                                    shape =
                                                        RoundedCornerShape(
                                                            13.dp
                                                        )
                                                ),

                                            contentAlignment =
                                                Alignment.Center
                                        ) {

                                            // Use the selected
                                            // category emoji.
                                            if (
                                                cat.emoji.isNotBlank()
                                            ) {

                                                Text(
                                                    text = cat.emoji,
                                                    fontSize = 23.sp
                                                )

                                            } else {

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

                                    // =================================
                                    // CATEGORY NAME AND PROGRESS
                                    // =================================

                                    Column {

                                        Text(
                                            text = cat.name,

                                            fontWeight =
                                                FontWeight.Bold,

                                            fontSize = 15.sp,

                                            lineHeight = 19.sp,

                                            maxLines = 2,

                                            overflow =
                                                TextOverflow.Ellipsis,

                                            color = Color(0xFF0F172A)
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.height(4.dp)
                                        )

                                        Text(
                                            text =
                                                "$totalTasks Tasks • $progressPct% Done",

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
                                                (
                                                        progressPct / 100f
                                                        ).coerceIn(
                                                        0f,
                                                        1f
                                                    )
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
                    }

                    item(
                        span = {
                            androidx.compose.foundation.lazy.grid
                                .GridItemSpan(maxLineSpan)
                        }
                    ) {

                        Spacer(
                            modifier = Modifier.height(80.dp)
                        )
                    }
                }
            }
        }

        // =============================================
        // ADD CATEGORY BOTTOM SHEET
        // =============================================

        if (showAddCategoryModal) {

            AddCategoryBottomSheet(
                onDismiss = {
                    showAddCategoryModal = false
                },

                onSave = { name, emoji, color ->

                    vm.addCategory(
                        name = name,
                        emoji = emoji,
                        color = color
                    )

                    showAddCategoryModal = false
                }
            )
        }
    }
}
