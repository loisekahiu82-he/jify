package com.example.gigify.Data

data class Worker(
    val userId: String = "",
    val profession: String = "",
    val bio: String = "",
    val experienceYears: Int = 0,
    val hourlyRate: Double = 0.0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val skills: List<String> = emptyList(),
    val isAvailable: Boolean = true
)