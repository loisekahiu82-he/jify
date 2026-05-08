package com.example.gigify.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gigify.Screens.Screens.*
import com.example.gigify.Screens.Screens.Dashboard.*
import com.example.gigify.Screens.Screens.Login.LoginScreen

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
        composable(ROUTE_REGISTER) { Register(navController) }
        composable(ROUTE_HOME) { Home(navController) }
        
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
            PostJobScreen(navController, category)
        }

        composable(ROUTE_EDIT_PROFILE) { EditProfile(navController) }

        composable(
            route = "$ROUTE_PAYMENT_METHOD/{jobId}/{amount}",
            arguments = listOf(
                navArgument("jobId") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            val amount = backStackEntry.arguments?.getString("amount") ?: ""
            PaymentMethodScreen(navController, jobId, amount)
        }

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
        
        composable(ROUTE_CHAT_LIST) { ChatListScreen(navController) }

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
            route = "$ROUTE_JOB_DETAIL/{jobId}",
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
    }
}
