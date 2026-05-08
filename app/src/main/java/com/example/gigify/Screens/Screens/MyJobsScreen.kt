package com.example.gigify.Screens.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gigify.Models.Job
import com.example.gigify.Models.ViewModels.AuthViewModel
import com.example.gigify.Models.ViewModels.JobViewModel
import com.example.gigify.Navigation.ROUTE_JOB_DETAIL
import com.example.gigify.Navigation.ROUTE_PAYMENT
import com.example.gigify.R
import com.example.gigify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyJobsScreen(navController: NavController) {
    val jobViewModel: JobViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val jobs by jobViewModel.jobs.collectAsState()
    val userData by authViewModel.currentUserData.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.gigify_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (userData?.name?.isNotEmpty() == true) "${userData?.name}'s Requests" else "My Job Requests",
                            color = Color.Black, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            userData?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Hello, ${it.name}!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkPurple
                )
                Text(
                    text = "Manage your job requests here.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (jobs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No job requests found", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(jobs) { job ->
                        JobItem(
                            job = job,
                            navController = navController,
                            onClick = { navController.navigate("${ROUTE_JOB_DETAIL}/${job.jobId}") },
                            onDelete = { jobViewModel.deleteJob(job.jobId, context) },
                            onCancel = { jobViewModel.cancelJob(job.jobId, context) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JobItem(job: Job, navController: NavController, onClick: () -> Unit, onDelete: () -> Unit, onCancel: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = job.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when (job.status.lowercase()) {
                            "pending" -> Color(0xFFFFF3E0)
                            "ongoing" -> Color(0xFFE3F2FD)
                            "cancelled" -> Color(0xFFFFEBEE)
                            else -> Color(0xFFE8F5E9)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = job.status.replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            color = when (job.status.lowercase()) {
                                "pending" -> Color(0xFFE65100)
                                "ongoing" -> Color(0xFF1565C0)
                                "cancelled" -> Color(0xFFC62828)
                                else -> Color(0xFF2E7D32)
                            },
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.Gray)
                        }
                        DropdownMenu(
                            expanded = showMenu, 
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(White)
                        ) {
                            if (job.status != "cancelled") {
                                DropdownMenuItem(
                                    text = { Text("Cancel Job", color = Color.Black) },
                                    onClick = { 
                                        onCancel()
                                        showMenu = false 
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Delete Request", color = Color.Red) },
                                onClick = { 
                                    onDelete()
                                    showMenu = false 
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = job.category, color = DarkPurple, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(text = "BUDGET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text(
                        text = "KES ${job.budget}",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }
                
                if (job.status.lowercase() == "pending" || job.status.lowercase() == "ongoing") {
                    Button(
                        onClick = { 
                            navController.navigate("${ROUTE_PAYMENT}/${job.jobId}/${job.budget}") 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp), tint = White)
                        Spacer(Modifier.width(8.dp))
                        Text("Pay Now", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
