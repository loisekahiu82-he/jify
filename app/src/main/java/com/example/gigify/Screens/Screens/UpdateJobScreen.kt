package com.example.gigify.Screens.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gigify.Models.ViewModels.JobViewModel
import com.example.gigify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateJobScreen(navController: NavController, jobId: String) {
    val context = LocalContext.current
    val jobViewModel: JobViewModel = viewModel()
    val job by jobViewModel.currentJob.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    LaunchedEffect(jobId) {
        jobViewModel.getJobDetails(jobId)
    }

    LaunchedEffect(job) {
        job?.let {
            title = it.title
            description = it.description
            budget = it.budget.toString()
            category = it.category
            status = it.status
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Update Job", color = AppPrimary, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = AppPrimary.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppPrimary,
                        unfocusedBorderColor = AppSurface,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = AppPrimary,
                        unfocusedLabelColor = AppPrimary.copy(alpha = 0.6f),
                        focusedContainerColor = AppSurface.copy(alpha = 0.9f),
                        unfocusedContainerColor = AppSurface.copy(alpha = 0.9f)
                    )
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category", color = AppPrimary.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppPrimary,
                        unfocusedBorderColor = AppSurface,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = AppPrimary,
                        unfocusedLabelColor = AppPrimary.copy(alpha = 0.6f),
                        focusedContainerColor = AppSurface.copy(alpha = 0.9f),
                        unfocusedContainerColor = AppSurface.copy(alpha = 0.9f)
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = AppPrimary.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppPrimary,
                        unfocusedBorderColor = AppSurface,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = AppPrimary,
                        unfocusedLabelColor = AppPrimary.copy(alpha = 0.6f),
                        focusedContainerColor = AppSurface.copy(alpha = 0.9f),
                        unfocusedContainerColor = AppSurface.copy(alpha = 0.9f)
                    )
                )

                OutlinedTextField(
                    value = budget,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) budget = it },
                    label = { Text("Budget (KES)", color = AppPrimary.copy(alpha = 0.7f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppPrimary,
                        unfocusedBorderColor = AppSurface,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = AppPrimary,
                        unfocusedLabelColor = AppPrimary.copy(alpha = 0.6f),
                        focusedContainerColor = AppSurface.copy(alpha = 0.9f),
                        unfocusedContainerColor = AppSurface.copy(alpha = 0.9f)
                    )
                )

                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status", color = AppPrimary.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppPrimary,
                        unfocusedBorderColor = AppSurface,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = AppPrimary,
                        unfocusedLabelColor = AppPrimary.copy(alpha = 0.6f),
                        focusedContainerColor = AppSurface.copy(alpha = 0.9f),
                        unfocusedContainerColor = AppSurface.copy(alpha = 0.9f)
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (title.isBlank() || description.isBlank() || budget.isBlank()) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Job updated successfully", Toast.LENGTH_SHORT).show()
                            navController.navigateUp()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Update Job", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
