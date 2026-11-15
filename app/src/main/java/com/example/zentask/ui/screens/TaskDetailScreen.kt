
package com.example.zentask.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.zentask.model.TaskStatus
import com.example.zentask.viewmodel.ZenTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel,
    taskId: String
) {
    val tasks by vm.tasks.collectAsState()

    val task = tasks.find {
        it.id == taskId
    }

    val urgency by vm.urgencyResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Task Detail")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (task == null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
            ) {

                Text("Task tidak ditemukan")

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text("Kembali")
                }
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // TASK INFORMATION HEADER
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = task.categoryName,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (task.description.isNotBlank()) {
                            Text(task.description)
                        }
                    }
                }

                // TASK DETAILS
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = "Task Information",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Category: ${task.categoryName}"
                        )

                        Text(
                            text = "Priority: ${task.priority.label}"
                        )

                        Text(
                            text = "Deadline: ${task.dueText}"
                        )

                        Text(
                            text = "Status: ${task.status.label}"
                        )

                        Text(
                            text = "Urgency: ${urgency[task.id]?.level ?: "Low"}"
                        )
                    }
                }

                if (task.status == TaskStatus.COMPLETED) {
                    Text("This task is completed.")
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                // EDIT TASK BUTTON
                Button(
                    onClick = {
                        navController.navigate(
                            "edit_task/${task.id}"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text("Edit Task")
                }
            }
        }
    }
}
