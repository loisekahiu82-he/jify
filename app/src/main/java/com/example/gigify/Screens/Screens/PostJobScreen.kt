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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gigify.Navigation.ROUTE_MY_JOBS
import com.example.gigify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(navController: NavController, initialCategory: String = "") {
    val context = LocalContext.current
    var jobTitle by remember { mutableStateOf("") }
    var jobDesc by remember { mutableStateOf("") }
    var jobBudget by remember { mutableStateOf("") }
    var jobCategory by remember { mutableStateOf(initialCategory) }
    var loading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Post a New Job", color = AppPrimary, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = AppPrimary)
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
                    value = jobTitle,
                    onValueChange = { jobTitle = it },
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
                    value = jobCategory,
                    onValueChange = { jobCategory = it },
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
                    value = jobDesc,
                    onValueChange = { jobDesc = it },
                    label = { Text("Description", color = AppPrimary.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth().height(130.dp),
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
                    value = jobBudget,
                    onValueChange = { if (it.all { char -> char.isDigit() }) jobBudget = it },
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

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { 
                        if (jobTitle.isBlank() || jobDesc.isBlank() || jobBudget.isBlank()) {
                            Toast.makeText(context, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                        } else {
                            loading = true
                            Toast.makeText(context, "Job posted successfully!", Toast.LENGTH_SHORT).show()
                            navController.navigate(ROUTE_MY_JOBS) {
                                popUpTo(0)
                            }
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (loading) CircularProgressIndicator(color = AppBackground, modifier = Modifier.size(24.dp))
                    else Text("Post Job", color = AppBackground, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostJobPreview() {
    GigifyTheme {
        PostJobScreen(navController = rememberNavController())
    }
}
