package com.example.gigify.Models

data class Review(
    val reviewId: String = "",
    val jobId: String = "",
    val reviewerId: String = "",
    val revieweeId: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
