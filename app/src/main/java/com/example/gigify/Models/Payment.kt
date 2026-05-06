package com.example.gigify.Models

data class Payment(
    val paymentId: String = "",
    val jobId: String = "",
    val clientId: String = "",
    val workerId: String = "",
    val amount: Double = 0.0,
    val status: String = "pending",
    val method: String = "M-Pesa",
    val transactionId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
