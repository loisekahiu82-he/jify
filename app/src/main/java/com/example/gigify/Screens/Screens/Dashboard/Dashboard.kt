package com.example.gigify.Screens.Screens.Dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gigify.Models.ViewModels.AuthViewModel
import com.example.gigify.Models.ViewModels.DashboardViewModel
import com.example.gigify.Navigation.*
import com.example.gigify.R
import com.example.gigify.ui.theme.*

@Composable
fun ClientDashboardScreen(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val dashboardVm: DashboardViewModel = viewModel()
    val stats by dashboardVm.clientStats.collectAsState()
    val currentUser by authViewModel.currentUserData.collectAsState()
    val fullName = currentUser?.name ?: "User"
    val firstName = fullName.split(" ").firstOrNull() ?: "User"

    LaunchedEffect(Unit) {
        dashboardVm.fetchClientStats()
    }

    DashboardContent(
        title = if (fullName != "User") "$fullName's Dashboard" else "Client Dashboard",
        userName = firstName,
        navController = navController,
        statsValue = "KES ${stats.totalSpent}",
        statsLabel = "Total Spent",
        isWorker = false,
        onLogout = { authViewModel.logout(navController) }
    )
}

@Composable
fun WorkerDashboardScreen(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val dashboardVm: DashboardViewModel = viewModel()
    val stats by dashboardVm.workerStats.collectAsState()
    val currentUser by authViewModel.currentUserData.collectAsState()
    val fullName = currentUser?.name ?: "User"
    val firstName = fullName.split(" ").firstOrNull() ?: "User"

    LaunchedEffect(Unit) {
        dashboardVm.fetchWorkerStats()
    }

    DashboardContent(
        title = if (fullName != "User") "$fullName's Dashboard" else "Worker Dashboard",
        userName = firstName,
        navController = navController,
        statsValue = "KES ${stats.totalEarnings}",
        statsLabel = "Total Earnings",
        isWorker = true,
        onLogout = { authViewModel.logout(navController) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    title: String,
    userName: String,
    navController: NavController,
    statsValue: String,
    statsLabel: String,
    isWorker: Boolean,
    onLogout: () -> Unit
) {
    var selectedItem by remember { mutableIntStateOf(1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppPrimary) },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = AppPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(65.dp)
                ) {
                    NavigationBarItem(
                        selected = selectedItem == 0,
                        onClick = { selectedItem = 0; navController.navigate(ROUTE_HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Market", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AppPrimary,
                            selectedTextColor = AppPrimary,
                            unselectedIconColor = AppSurface,
                            unselectedTextColor = AppSurface,
                            indicatorColor = AppSurface.copy(alpha = 0.3f)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedItem == 1,
                        onClick = { selectedItem = 1 },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        label = { Text("Dashboard", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AppPrimary,
                            selectedTextColor = AppPrimary,
                            unselectedIconColor = AppSurface,
                            unselectedTextColor = AppSurface,
                            indicatorColor = AppSurface.copy(alpha = 0.3f)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedItem == 2,
                        onClick = { selectedItem = 2; navController.navigate(ROUTE_EDIT_PROFILE) },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profile", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AppPrimary,
                            selectedTextColor = AppPrimary,
                            unselectedIconColor = AppSurface,
                            unselectedTextColor = AppSurface,
                            indicatorColor = AppSurface.copy(alpha = 0.3f)
                        )
                    )
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.gigify_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(80.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Habari, $userName!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = AppPrimary)
                Text(text = "Here is your account overview", fontSize = 14.sp, color = AppPrimary.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = statsLabel, color = Color.Black.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text(text = statsValue, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(color = Color.Black.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                            Text(text = "Status: Active", color = Color.Black, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Quick Actions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppPrimary)
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (isWorker) {
                        DashboardItem(modifier = Modifier.weight(1f), text = "Find Gigs", icon = Icons.Default.Search, containerColor = AppSurface, contentColor = Color.Black, onClick = { navController.navigate(ROUTE_HOME) })
                    } else {
                        DashboardItem(modifier = Modifier.weight(1f), text = "Post Job", icon = Icons.Default.Add, containerColor = AppSurface, contentColor = Color.Black, onClick = { navController.navigate(ROUTE_POST_JOB) })
                    }
                    DashboardItem(modifier = Modifier.weight(1f), text = "My Gigs", icon = Icons.AutoMirrored.Filled.List, containerColor = AppSurface, contentColor = Color.Black, onClick = { navController.navigate(ROUTE_MY_JOBS) })
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardItem(modifier = Modifier.weight(1f), text = "Messages", icon = Icons.Default.Email, containerColor = AppSurface, contentColor = Color.Black, onClick = { /* Navigate to Chat List */ })
                    DashboardItem(modifier = Modifier.weight(1f), text = "Payments", icon = Icons.Default.Payments, containerColor = AppSurface, contentColor = Color.Black, onClick = { /* Navigate to Transactions */ })
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardItem(modifier = Modifier.weight(1f), text = "Profile", icon = Icons.Default.Person, containerColor = AppSurface, contentColor = Color.Black, onClick = { navController.navigate(ROUTE_EDIT_PROFILE) })
                    DashboardItem(modifier = Modifier.weight(1f), text = "Settings", icon = Icons.Default.Settings, containerColor = AppSurface, contentColor = Color.Black, onClick = { navController.navigate(ROUTE_EDIT_PROFILE) })
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DashboardItem(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(100.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = text, tint = contentColor, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, color = contentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
    GigifyTheme {
        DashboardContent(
            title = "James's Dashboard",
            navController = rememberNavController(),
            userName = "James",
            statsValue = "KES 1000",
            statsLabel = "Total Spent",
            isWorker = false,
            onLogout = {}
        )
    }
}
