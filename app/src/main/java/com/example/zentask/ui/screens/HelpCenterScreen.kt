package com.example.zentask.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.zentask.ZenTaskTheme
import com.example.zentask.ui.components.GlassCard
import com.example.zentask.ui.components.ZenBackground

@Composable
fun HelpCenterScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    var expandedIndex by remember { mutableStateOf(0) }

    val guides = listOf(
        Pair("📋 My Tasks & Collab Guide", "Learn how to create tasks, select priorities (Low, Medium, High), and invite peers using 6-digit collab codes sent via email."),
        Pair("🔥 Vital Task Automation (<48h)", "ZenTask automatically detects any task flagged with High priority that is due within 48 hours and moves it into your urgent vital widget."),
        Pair("🎨 Custom Pastel Color Swatches", "Assign aesthetic pastel color chips to distinguish Study, Work, and Personal spaces with high clarity."),
        Pair("📊 Dashboard & Accumulation Chart", "Visualizes your weekly productivity ring across Completed, In-Progress, and Not-Started tasks."),
        Pair("👤 Security & Encryption", "ZenTask ensures 256-bit client encryption for cloud sync and offline data sovereignty.")
    )

    ZenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ZenTaskTheme.TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Help Center & Guides", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ZenTaskTheme.TextPrimary)
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search feature guides...") },
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(guides) { index, (title, content) ->
                    val isExp = expandedIndex == index
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedIndex = if (isExp) -1 else index }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ZenTaskTheme.TextPrimary)
                            Text(if (isExp) "▲" else "▼", fontSize = 12.sp, color = ZenTaskTheme.TextSecondary)
                        }
                        if (isExp) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(content, fontSize = 12.sp, color = ZenTaskTheme.TextSecondary, lineHeight = 18.sp)
                        }
                    }
                }
            }
        }
    }
}
