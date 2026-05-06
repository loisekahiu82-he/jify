package com.example.gigify.Utils

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

// Toast Extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// Date Extensions
fun Long.toDateString(pattern: String = "dd MMM yyyy"): String {
    val date = Date(this)
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(date)
}

fun Long.toTimeString(): String {
    val date = Date(this)
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(date)
}

// Currency Extensions
fun Double.toCurrency(symbol: String = "Ksh"): String {
    return "$symbol ${String.format("%.2f", this)}"
}

// String Extensions
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
