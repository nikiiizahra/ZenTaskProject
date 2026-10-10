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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.zentask.ZenTaskTheme
import com.example.zentask.ui.components.GlassCard
import com.example.zentask.ui.components.ZenBackground

@Composable
fun HelpCenterScreen(
    navController: NavHostController
) {
    var searchQuery by remember {
        mutableStateOf("")
    }

    var expandedIndex by remember {
        mutableStateOf<Int?>(null)
    }

    val guides = listOf(
        Pair(
            "📋 My Tasks & Collab Guide",
            "Learn how to create tasks, select priorities (Low, Medium, High), and invite peers using 6-digit collab codes sent via email."
        ),

        Pair(
            "🔥 Vital Task Automation (<48h)",
            "ZenTask automatically detects any task flagged with High priority that is due within 48 hours and moves it into your urgent vital widget."
        ),

        Pair(
            "🎨 Custom Pastel Color Swatches",
            "Assign aesthetic pastel color chips to distinguish Study, Work, and Personal spaces with high clarity."
        ),

        Pair(
            "📊 Dashboard & Accumulation Chart",
            "Visualizes your weekly productivity ring across Completed, In-Progress, and Not-Started tasks."
        ),

        Pair(
            "👤 Security & Encryption",
            "ZenTask ensures 256-bit client encryption for cloud sync and offline data sovereignty."
        )
    )

    val filteredGuides = guides.filter { guide ->
        guide.first.contains(
            searchQuery,
            ignoreCase = true
        ) ||
                guide.second.contains(
                    searchQuery,
                    ignoreCase = true
                )
    }

    ZenBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,

                        contentDescription = "Back",

                        tint = Color(0xFF0F172A)
                    )
                }

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Column {

                    Text(
                        text = "Help Center & Guides",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Text(
                        text = "Find answers and learn ZenTask features",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            OutlinedTextField(
                value = searchQuery,

                onValueChange = {
                    searchQuery = it

                    // Tutup FAQ yang sedang terbuka
                    // ketika pencarian berubah
                    expandedIndex = null
                },

                modifier = Modifier.fillMaxWidth(),

                placeholder = {
                    Text(
                        text = "Search feature guides...",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                },

                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                ),

                singleLine = true,

                shape = RoundedCornerShape(18.dp),

                leadingIcon = {

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = ZenTaskTheme.Primary
                    )
                }
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Feature Guides",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            if (filteredGuides.isEmpty()) {

                Surface(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(20.dp),

                    color = Color.White
                ) {

                    Column(
                        modifier = Modifier.padding(24.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "No guides found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = "Try searching with another keyword.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

            } else {

                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp),

                    modifier = Modifier.fillMaxSize(),

                    contentPadding =
                        PaddingValues(bottom = 24.dp)
                ) {

                    itemsIndexed(filteredGuides) {
                            index,
                            guide ->

                        val title = guide.first
                        val content = guide.second

                        val isExpanded =
                            expandedIndex == index

                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                    expandedIndex =
                                        if (isExpanded) {
                                            null
                                        } else {
                                            index
                                        }
                                }
                        ) {

                            Row(
                                modifier =
                                    Modifier.fillMaxWidth(),

                                horizontalArrangement =
                                    Arrangement.SpaceBetween,

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Text(
                                    text = title,

                                    modifier =
                                        Modifier.weight(1f),

                                    fontWeight =
                                        FontWeight.Bold,

                                    fontSize = 15.sp,

                                    color =
                                        Color(0xFF0F172A)
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(8.dp)
                                )

                                Text(
                                    text =
                                        if (isExpanded) {
                                            "▲"
                                        } else {
                                            "▼"
                                        },

                                    fontSize = 13.sp,

                                    color =
                                        ZenTaskTheme.Primary
                                )
                            }

                            if (isExpanded) {

                                Spacer(
                                    modifier =
                                        Modifier.height(10.dp)
                                )

                                HorizontalDivider(
                                    color =
                                        Color(0xFFE2E8F0)
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(10.dp)
                                )

                                Text(
                                    text = content,

                                    fontSize = 14.sp,

                                    color =
                                        Color(0xFF475569),

                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}