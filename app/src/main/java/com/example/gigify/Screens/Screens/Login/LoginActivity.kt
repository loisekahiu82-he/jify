package com.example.gigify.Screens.Screens.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gigify.Navigation.ROUTE_HOME
import com.example.gigify.ui.theme.*

@Composable
fun LoginActivityScreen(navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(LightPurple, White, DarkPurple.copy(alpha = 0.1f))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (!isOtpSent) "Phone Login" else "Verify OTP ",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = DarkPurple
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (!isOtpSent) "Enter your phone number to continue" else "Enter the code sent to $phoneNumber",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isOtpSent) {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                placeholder = { Text("+254...") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkPurple,
                    unfocusedBorderColor = LightPurple
                )
            )
        } else {
            OutlinedTextField(
                value = otpCode,
                onValueChange = { otpCode = it },
                label = { Text("OTP Code") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DarkPurple,
                    unfocusedBorderColor = LightPurple
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (!isOtpSent) {
                    isOtpSent = true
                } else {
                    navController.navigate(ROUTE_HOME)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (!isOtpSent) "Send OTP" else "Verify & Login",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
        }

        if (isOtpSent) {
            TextButton(onClick = { isOtpSent = false }) {
                Text("Change Phone Number", color = DarkPurple)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginActivityScreenPreview() {
    GigifyTheme {
        LoginActivityScreen(rememberNavController())
    }
}
