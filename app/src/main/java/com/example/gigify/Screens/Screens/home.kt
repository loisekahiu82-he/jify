package com.example.gigify.Screens.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gigify.R
import com.example.gigify.Models.ViewModels.AuthViewModel
import com.example.gigify.Navigation.ROUTE_POST_JOB
import com.example.gigify.ui.components.CategoryChip
import com.example.gigify.ui.components.InfoBanner
import com.example.gigify.ui.components.WorkerCard
import com.example.gigify.ui.components.WorkerDisplay
import com.example.gigify.ui.theme.DarkPurple
import com.example.gigify.ui.theme.LightPurple
import com.example.gigify.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUserData.collectAsState()
    val firstName = currentUser?.name?.split(" ")?.firstOrNull() ?: "User"

    val workers = listOf(
        WorkerDisplay("John Mwangi", "Plumber", "1.2km away", "4.8", "42", "Available now", "JM"),
        WorkerDisplay("Amina Kioni", "Cleaner", "0.8km away", "4.9", "87", "Available now", "AK"),
        WorkerDisplay("Peter Otieno", "Painter", "2.1km away", "4.6", "29", "Busy till 3pm", "PO", "Later", true)
    )

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Plumber", "Cleaner", "Painter", "Driver")

    val filteredWorkers = workers.filter { worker ->
        val matchesCategory = selectedCategory == "All" || worker.profession.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isEmpty() || 
                           worker.name.contains(searchQuery, ignoreCase = true) || 
                           worker.profession.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(shadowElevation = 4.dp) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.gigify_logo),
                                contentDescription = "Gigify Logo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gigify",
                                color = DarkPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
                )
            }
        },
        containerColor = White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ROUTE_POST_JOB) },
                containerColor = DarkPurple,
                contentColor = White
            ) {
                Text("+", fontSize = 24.sp)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Habari, $firstName",
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Find trusted workers near you",
                color = Color.Black.copy(alpha = 0.6f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search e.g. plumber, cleaner...", color = DarkPurple.copy(alpha = 0.4f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DarkPurple) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = LightPurple,
                    focusedBorderColor = DarkPurple,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Categories", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    CategoryChip(
                        label = category,
                        isSelected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                if (filteredWorkers.isEmpty()) "No workers found" else "Available near you", 
                color = Color.Black,
                fontWeight = FontWeight.Bold, 
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredWorkers) { worker ->
                    WorkerCard(worker, navController)
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoBanner()
                }
            }
        }
    }
}
