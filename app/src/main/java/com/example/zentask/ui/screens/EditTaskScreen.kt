
package com.example.zentask.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.zentask.model.TaskPriority
import com.example.zentask.viewmodel.ZenTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    navController: NavHostController,
    vm: ZenTaskViewModel,
    taskId: String
) {
    val tasks by vm.tasks.collectAsState()
    val task = tasks.find { it.id == taskId }

    var title by remember(taskId) {
        mutableStateOf(task?.title.orEmpty())
    }

    var description by remember(taskId) {
        mutableStateOf(task?.description.orEmpty())
    }

    var category by remember(taskId) {
        mutableStateOf(task?.categoryName.orEmpty())
    }

    var priority by remember(taskId) {
        mutableStateOf(task?.priority ?: TaskPriority.MEDIUM)
    }

    var deadline by remember(taskId) {
        mutableStateOf(task?.dueText.orEmpty())
    }

    var showPriorityMenu by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Edit Task")
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

            Text(
                text = "Task tidak ditemukan",
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
            )

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // TASK TITLE
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = {
                        Text("Task Title")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // DESCRIPTION
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Description")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                // CATEGORY
                OutlinedTextField(
                    value = category,
                    onValueChange = {
                        category = it
                    },
                    label = {
                        Text("Category")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // PRIORITY
                ExposedDropdownMenuBox(
                    expanded = showPriorityMenu,
                    onExpandedChange = {
                        showPriorityMenu = !showPriorityMenu
                    }
                ) {

                    OutlinedTextField(
                        value = priority.label,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Priority")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = showPriorityMenu
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = showPriorityMenu,
                        onDismissRequest = {
                            showPriorityMenu = false
                        }
                    ) {

                        TaskPriority.values().forEach { option ->

                            DropdownMenuItem(
                                text = {
                                    Text(option.label)
                                },
                                onClick = {
                                    priority = option
                                    showPriorityMenu = false
                                }
                            )
                        }
                    }
                }

                // DEADLINE
                OutlinedTextField(
                    value = deadline,
                    onValueChange = {
                        deadline = it
                    },
                    label = {
                        Text("Deadline (dd/MM/yyyy)")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // ERROR MESSAGE
                if (error.isNotBlank()) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // SAVE CHANGES
                Button(
                    onClick = {

                        when {

                            title.isBlank() -> {
                                error = "Task Title wajib diisi"
                            }

                            category.isBlank() -> {
                                error = "Category wajib diisi"
                            }

                            else -> {

                                val saved = vm.updateTaskDetails(
                                    taskId = taskId,
                                    title = title,
                                    description = description,
                                    categoryName = category,
                                    priority = priority,
                                    dueText = deadline
                                )

                                if (saved) {
                                    navController.popBackStack()
                                } else {
                                    error = "Task gagal diperbarui"
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}
