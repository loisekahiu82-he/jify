package com.example.gigify.Models

data class Message(
    val messageId: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val text: String = "",
    val type: String = "text",
    val isRead: Boolean = false,
    val sentAt: Long = System.currentTimeMillis()
)
