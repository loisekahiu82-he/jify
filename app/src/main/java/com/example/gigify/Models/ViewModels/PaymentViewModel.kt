package com.example.gigify.Models.ViewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigify.Models.Payment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments

    init {
        fetchPayments()
    }

    fun fetchPayments() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("Payments")
            .whereEqualTo("clientId", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.toObject(Payment::class.java) } ?: emptyList()
                _payments.value = list
            }
    }

    fun recordPayment(payment: Payment, context: Context, onSuccess: () -> Unit) {
        val paymentId = db.collection("Payments").document().id
        val finalPayment = payment.copy(paymentId = paymentId)
        
        db.collection("Payments").document(paymentId).set(finalPayment)
            .addOnSuccessListener {
                // Also update the job status
                db.collection("Jobs").document(payment.jobId).update("status", "paid")
                Toast.makeText(context, "Payment recorded successfully", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to record payment", Toast.LENGTH_SHORT).show()
            }
    }
}
