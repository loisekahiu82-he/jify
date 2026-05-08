package com.example.gigify.Screens.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.gigify.Models.ViewModels.AuthViewModel
import com.example.gigify.Navigation.ROUTE_POST_JOB
import com.example.gigify.R
import com.example.gigify.ui.components.CategoryChip
import com.example.gigify.ui.components.InfoBanner
import com.example.gigify.ui.components.WorkerCard
import com.example.gigify.ui.components.WorkerDisplay
import com.example.gigify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    authVm: AuthViewModel = viewModel()
) {
    val user by authVm.currentUserData.collectAsState()
    val firstName = user?.name?.split(" ")?.firstOrNull() ?: "User"

    val workers = listOf(
        WorkerDisplay("John Mwangi", "Plumber", "1.2km away", "4.8", "42", "Available now", "JM"),
        WorkerDisplay("Amina Kioni", "Cleaner", "0.8km away", "4.9", "87", "Available now", "AK"),
        WorkerDisplay(
            "Peter Otieno",
            "Painter",
            "2.1km away",
            "4.6",
            "29",
            "Busy till 3pm",
            "PO",
            "Later",
            true
        )
    )

    var search by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("All") }
    val categories = listOf("All", "Plumber", "Cleaner", "Painter", "Driver")

    val filteredList = workers.filter { w ->
        val catMatch = category == "All" || w.profession.equals(category, ignoreCase = true)
        val searchMatch = search.isEmpty() ||
                w.name.contains(search, ignoreCase = true) ||
                w.profession.contains(search, ignoreCase = true)
        catMatch && searchMatch
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppGradient)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.gigify_logo),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gigify",
                                color = AppPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(ROUTE_POST_JOB) },
                    containerColor = AppPrimary,
                    contentColor = AppBackground
                ) {
                    Text("+", fontSize = 24.sp, color = AppBackground)
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Habari, $firstName",
                    color = AppPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Find trusted workers near you",
                    color = AppPrimary.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Search e.g. plumber, cleaner...",
                            color = Color.Black.copy(alpha = 0.4f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Black.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppPrimary,
                        unfocusedBorderColor = AppSurface,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = AppPrimary,
                        unfocusedLabelColor = AppPrimary.copy(alpha = 0.6f),
                        focusedContainerColor = AppSurface.copy(alpha = 0.9f),
                        unfocusedContainerColor = AppSurface.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Categories",
                    color = AppPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { c ->
                        CategoryChip(
                            label = c,
                            isSelected = category == c,
                            onClick = { category = c }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    if (filteredList.isEmpty()) "No workers found" else "Available near you",
                    color = AppPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredList) { w ->
                        WorkerCard(w, navController)
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        InfoBanner()
                    }
                }
            }
        }
    }
}