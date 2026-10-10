package com.example.zentask.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    navController: NavHostController
) {
    var title by remember {
        mutableStateOf("API Token Integration")
    }

    var description by remember {
        mutableStateOf("Integrate authentication token into application API.")
    }

    var category by remember {
        mutableStateOf("Development")
    }

    var priority by remember {
        mutableStateOf("High")
    }

    var deadline by remember {
        mutableStateOf("12 October 2026")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Task",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
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
                            contentDescription = "Back"
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                label = {
                    Text(
                        text = "Task Title",
                        fontSize = 14.sp
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                },
                label = {
                    Text(
                        text = "Description",
                        fontSize = 14.sp
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 90.dp),
                minLines = 3
            )

            OutlinedTextField(
                value = category,
                onValueChange = {
                    category = it
                },
                label = {
                    Text(
                        text = "Category",
                        fontSize = 14.sp
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = priority,
                onValueChange = {
                    priority = it
                },
                label = {
                    Text(
                        text = "Priority",
                        fontSize = 14.sp
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = deadline,
                onValueChange = {
                    deadline = it
                },
                label = {
                    Text(
                        text = "Deadline",
                        fontSize = 14.sp
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}