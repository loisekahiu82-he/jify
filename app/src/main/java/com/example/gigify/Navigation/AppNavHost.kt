package com.example.gigify.Navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gigify.Screens.Screens.*
import com.example.gigify.screens.RegisterScreen
import com.example.gigify.screens.SplashScreen
import com.example.gigify.screens.screens.dashboard.*
import com.example.gigify.Screens.Screens.Login.LoginScreen
import com.example.gigify.ui.theme.DarkPurple

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(ROUTE_SPLASH) { SplashScreen(navController) }
        composable(ROUTE_LOGIN) { LoginScreen(navController) }
        composable(ROUTE_REGISTER) { RegisterScreen(navController) }
        composable(ROUTE_HOME) { Home(navController) }
        
        // Updated ROUTE_POST_JOB to accept an optional category
        composable(
            route = "$ROUTE_POST_JOB?category={category}",
            arguments = listOf(
                navArgument("category") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null 
                }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            PostJob(navController, category)
        }

        composable(ROUTE_EDIT_PROFILE) { EditProfile(navController) }

        composable(
            route = "$ROUTE_PAYMENT/{jobId}/{amount}",
            arguments = listOf(
                navArgument("jobId") { type = NavType.StringType; defaultValue = "unknown" },
                navArgument("amount") { type = NavType.StringType; defaultValue = "0" }
            )
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: "unknown"
            val amount = backStackEntry.arguments?.getString("amount") ?: "0"
            MpesaPaymentScreen(navController, jobId, amount)
        }

        composable(ROUTE_MY_JOBS) { MyJobsScreen(navController) }
        
        composable(
            route = "$ROUTE_CHAT/{chatId}/{otherUserName}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("otherUserName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            val otherUserName = backStackEntry.arguments?.getString("otherUserName") ?: "User"
            ChatScreen(navController, chatId, otherUserName)
        }

        composable(ROUTE_CLIENT_DASHBOARD) { ClientDashboardScreen(navController) }
        composable(ROUTE_WORKER_DASHBOARD) { WorkerDashboardScreen(navController) }
        
        composable(
            route = "$ROUTE_VIEW_WORKER/{workerId}",
            arguments = listOf(navArgument("workerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("workerId") ?: ""
            ViewWorker(navController, id)
        }

        composable(
            route = "$ROUTE_VIEW_JOB/{jobId}",
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("jobId") ?: ""
            JobDetail(navController, id)
        }

        composable(
            route = "$ROUTE_UPDATE_JOB_STATUS/{jobId}",
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("jobId") ?: ""
            UpdateJobScreen(navController, id)
        }
        
        // Map common route aliases
        composable(ROUTE_JOB_DETAIL + "/{jobId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("jobId") ?: ""
            JobDetail(navController, id)
        }

        // Other utility screens
        composable(ROUTE_INFO) { UnderConstructionScreen("App Info", navController) }
        composable(ROUTE_NOTIFICATIONS) { UnderConstructionScreen("Notifications", navController) }
        composable(ROUTE_SETTINGS) { UnderConstructionScreen("Settings", navController) }
        composable(ROUTE_REVIEWS) { UnderConstructionScreen("Reviews", navController) }
    }
}

@Composable
fun UnderConstructionScreen(title: String, navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkPurple)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "This screen is currently under development.", color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.navigateUp() }) {
            Text("Go Back")
        }
    }
}
