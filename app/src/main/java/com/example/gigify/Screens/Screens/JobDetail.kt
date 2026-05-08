package com.example.gigify.Screens.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gigify.Models.Job
import com.example.gigify.Models.ViewModels.JobViewModel
import com.example.gigify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetail(navController: NavController, jobId: String) {
    val jobViewModel: JobViewModel = viewModel()
    val job by jobViewModel.currentJob.collectAsState()

    LaunchedEffect(jobId) {
        jobViewModel.getJobDetails(jobId)
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(LightPurple, White, MainPurple)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Details", color = Color.Black, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = White
    ) { innerPadding ->
        job?.let { currentJob ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundGradient)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // Show Job Image if it exists
                if (!currentJob.jobImage.isNullOrEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        AsyncImage(
                            model = currentJob.jobImage,
                            contentDescription = "Job Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = White.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(currentJob.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(currentJob.category, color = Color.Black.copy(alpha = 0.7f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text("STATUS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.6f))
                        Surface(
                            color = AppPrimary,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                currentJob.status.uppercase(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppBackground
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text("BUDGET", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.6f))
                        Text("KES ${currentJob.budget}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Job Description", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(
                    currentJob.description.ifEmpty { "No description provided." },
                    color = Color.Black.copy(alpha = 0.8f),
                    lineHeight = 22.sp,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                if (currentJob.status == "pending") {
                    Button(
                        onClick = { /* Accept or Edit logic */ },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Edit Job Post", color = AppBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AppPrimary)
        }
    }
}
