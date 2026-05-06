package com.example.gigify.Utils

object Constants {
    // Firestore Collections
    const val COLLECTION_USERS = "Users"
    const val COLLECTION_WORKERS = "Workers"
    const val COLLECTION_JOBS = "Jobs"
    const val COLLECTION_CHATS = "Chats"
    const val COLLECTION_MESSAGES = "Messages"
    const val COLLECTION_PAYMENTS = "Payments"

    // Job Statuses
    const val STATUS_PENDING = "pending"
    const val STATUS_ONGOING = "ongoing"
    const val STATUS_COMPLETED = "completed"
    const val STATUS_CANCELLED = "cancelled"

    // Roles
    const val ROLE_CLIENT = "client"
    const val ROLE_WORKER = "worker"

    // Notifications
    const val NOTIFICATION_CHANNEL_ID = "gigify_notifications"
    const val NOTIFICATION_CHANNEL_NAME = "Gigify Notifications"
}
