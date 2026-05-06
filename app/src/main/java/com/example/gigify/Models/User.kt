package com.example.gigify.Models

import com.google.firebase.firestore.PropertyName

data class User(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String? = null,
    val role: String = "",
    val profilePhoto: String = "",
    val locationName: String = "",
    
    @get:PropertyName("isVerified")
    @set:PropertyName("isVerified")
    var isVerified: Boolean = false,

    val fcmToken: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
