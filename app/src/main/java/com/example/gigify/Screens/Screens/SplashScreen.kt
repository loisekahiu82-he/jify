package com.example.gigify.Screens.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gigify.Navigation.ROUTE_HOME
import com.example.gigify.Navigation.ROUTE_REGISTER
import com.example.gigify.Navigation.ROUTE_SPLASH
import com.example.gigify.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(LightPurple, White, MainPurple)
    )

    LaunchedEffect(Unit) {
        delay(2000) 
        val currentUser = FirebaseAuth.getInstance().currentUser
        val destination = if (currentUser != null) ROUTE_HOME else ROUTE_REGISTER
        
        navController.navigate(destination) {
            popUpTo(ROUTE_SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Gigify",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = DarkPurple
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    GigifyTheme {
        SplashScreen(rememberNavController())
    }
}
