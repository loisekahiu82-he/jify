package com.example.gigify.Screens.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gigify.Models.ViewModels.MpesaViewModel
import com.example.gigify.ui.theme.*

/**
 * Simplified M-Pesa Payment Screen focused on STK Push integration.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MpesaPaymentScreen(navController: NavController, jobId: String, amount: String) {
    val viewModel: MpesaViewModel = viewModel()
    val context = LocalContext.current

    var phoneNumber by remember { mutableStateOf("") }
    val paymentStatus by viewModel.paymentStatus.collectAsState()

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(LightPurple, White, MainPurple)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("M-Pesa Payment", color = Color.Black, fontWeight = FontWeight.Bold) },
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
                .background(backgroundGradient)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Amount to Pay",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.6f)
            )
            Text(
                text = "KES $amount",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = White.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Payment via STK Push",
                        fontWeight = FontWeight.Bold,
                        color = DarkPurple,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Enter your phone number below. You will receive a prompt on your phone to enter your M-Pesa PIN.",
                        fontSize = 13.sp,
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("M-Pesa Number") },
                        placeholder = { Text("2547XXXXXXXX") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainPurple,
                            unfocusedBorderColor = LightPurple,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = DarkPurple
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { 
                    if (phoneNumber.isNotEmpty()) {
                        viewModel.initiateSTKPush(phoneNumber, amount.split(".")[0].toInt())
                    } else {
                        Toast.makeText(context, "Please enter your M-Pesa number", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Request Payment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            paymentStatus?.let { status ->
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (status.contains("Error")) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(16.dp),
                        color = if (status.contains("Error")) Color.Red else Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MpesaPaymentScreenPreview() {
    GigifyTheme {
        MpesaPaymentScreen(rememberNavController(), "job123", "500")
    }
}
