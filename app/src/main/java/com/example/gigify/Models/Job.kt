package com.example.gigify.Models

data class Job(
    val jobId: String = "",
    val clientId: String = "",
    val workerId: String? = null,
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val budget: Double = 0.0,
    val status: String = "pending",
    val location: String = "",
    val jobImage: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
