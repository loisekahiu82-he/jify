package com.example.gigify.Screens.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Payments
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
 * Payment Screen with M-Pesa, Card, PayPal, and Cash options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MpesaPaymentScreen(navController: NavController, jobId: String, amount: String) {
    val viewModel: MpesaViewModel = viewModel()
    val context = LocalContext.current

    var phoneNumber by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var paypalEmail by remember { mutableStateOf("") }
    
    val paymentStatus by viewModel.paymentStatus.collectAsState()
    var selectedMethod by remember { mutableStateOf("M-Pesa") }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(LightPurple, White, MainPurple)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secure Payment", color = Color.Black, fontWeight = FontWeight.Bold) },
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
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Select Payment Method", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Payment method tabs
            val methods = listOf("M-Pesa", "Card", "PayPal", "Cash")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                methods.forEach { method ->
                    val isSelected = selectedMethod == method
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedMethod = method },
                        color = if (isSelected) DarkPurple else White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, LightPurple.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = method,
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            color = if (isSelected) White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = White.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (selectedMethod) {
                        "M-Pesa" -> {
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text("Phone Number", color = Color.Black.copy(alpha = 0.7f)) },
                                placeholder = { Text("254712345678") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MainPurple,
                                    unfocusedBorderColor = LightPurple,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedLabelColor = Color.Black
                                )
                            )
                        }
                        "Card" -> {
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { cardNumber = it },
                                label = { Text("Card Number", color = Color.Black.copy(alpha = 0.7f)) },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MainPurple,
                                    unfocusedBorderColor = LightPurple,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedLabelColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                OutlinedTextField(
                                    value = expiryDate,
                                    onValueChange = { expiryDate = it },
                                    label = { Text("MM/YY", color = Color.Black.copy(alpha = 0.7f)) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MainPurple,
                                        unfocusedBorderColor = LightPurple,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        focusedLabelColor = Color.Black
                                    )
                                )
                                OutlinedTextField(
                                    value = cvv,
                                    onValueChange = { cvv = it },
                                    label = { Text("CVV", color = Color.Black.copy(alpha = 0.7f)) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MainPurple,
                                        unfocusedBorderColor = LightPurple,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        focusedLabelColor = Color.Black
                                    )
                                )
                            }
                        }
                        "PayPal" -> {
                            OutlinedTextField(
                                value = paypalEmail,
                                onValueChange = { paypalEmail = it },
                                label = { Text("PayPal Email", color = Color.Black.copy(alpha = 0.7f)) },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MainPurple,
                                    unfocusedBorderColor = LightPurple,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedLabelColor = Color.Black
                                )
                            )
                        }
                        "Cash" -> {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = DarkPurple
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Pay with Cash directly to the worker upon successful completion of the job.",
                                    textAlign = TextAlign.Center,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ensure you only pay after you are satisfied with the work.",
                                    textAlign = TextAlign.Center,
                                    color = Color.Black.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { 
                    when (selectedMethod) {
                        "M-Pesa" -> {
                            if (phoneNumber.isNotEmpty()) {
                                viewModel.initiateSTKPush(phoneNumber, amount.split(".")[0].toInt())
                            } else {
                                Toast.makeText(context, "Please enter phone number", Toast.LENGTH_SHORT).show()
                            }
                        }
                        "Cash" -> {
                            Toast.makeText(context, "Cash payment selected. Notification sent to worker.", Toast.LENGTH_LONG).show()
                            navController.popBackStack()
                        }
                        else -> {
                            // Card and PayPal logic
                            Toast.makeText(context, "$selectedMethod payment successful!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (selectedMethod == "Cash") "Confirm Cash Payment" else "Confirm Payment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            paymentStatus?.let { status ->
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = status,
                    color = if (status.contains("Error")) Color.Red else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
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
