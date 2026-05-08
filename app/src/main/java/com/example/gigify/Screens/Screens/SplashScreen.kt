package com.example.gigify.Screens.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gigify.Navigation.*
import com.example.gigify.R
import com.example.gigify.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(LightPurple, White, MainPurple)
    )

    LaunchedEffect(Unit) {
        delay(2000) 
        val currentUser = FirebaseAuth.getInstance().currentUser
        
        if (currentUser != null) {
            // Check user role to navigate to the correct dashboard
            FirebaseFirestore.getInstance().collection("Users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")?.lowercase()
                    val destination = when (role) {
                        "worker" -> ROUTE_WORKER_DASHBOARD
                        "client" -> ROUTE_CLIENT_DASHBOARD
                        else -> ROUTE_HOME
                    }
                    navController.navigate(destination) {
                        popUpTo(ROUTE_SPLASH) { inclusive = true }
                    }
                }
                .addOnFailureListener {
                    // If fetching role fails, fallback to login
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(ROUTE_SPLASH) { inclusive = true }
                    }
                }
        } else {
            navController.navigate(ROUTE_REGISTER) {
                popUpTo(ROUTE_SPLASH) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.gigify_logo),
                contentDescription = "Gigify Logo",
                modifier = Modifier.size(150.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Gigify",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = DarkPurple
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    GigifyTheme {
        SplashScreen(rememberNavController())
    }
}
