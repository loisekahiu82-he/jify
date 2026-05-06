package com.example.gigify.Models.ViewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DashboardViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _clientStats = MutableStateFlow(ClientStats())
    val clientStats: StateFlow<ClientStats> = _clientStats

    private val _workerStats = MutableStateFlow(WorkerStats())
    val workerStats: StateFlow<WorkerStats> = _workerStats

    data class ClientStats(
        val activeJobs: Int = 0,
        val totalSpent: Double = 0.0,
        val completedJobs: Int = 0
    )

    data class WorkerStats(
        val totalEarnings: Double = 0.0,
        val jobsDone: Int = 0,
        val averageRating: Double = 0.0
    )

    fun fetchClientStats() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("Jobs")
            .whereEqualTo("clientId", uid)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    val docs = querySnapshot.documents
                    val active = docs.count { it.getString("status") == "ongoing" }
                    val completed = docs.count { it.getString("status") == "completed" }
                    val spent = docs.filter { it.getString("status") == "completed" }
                        .sumOf { it.getDouble("budget") ?: 0.0 }
                    
                    _clientStats.value = ClientStats(active, spent, completed)
                }
            }
    }

    fun fetchWorkerStats() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("Jobs")
            .whereEqualTo("workerId", uid)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { querySnapshot ->
                    val docs = querySnapshot.documents
                    val done = docs.count { it.getString("status") == "completed" }
                    val earnings = docs.filter { it.getString("status") == "completed" }
                        .sumOf { it.getDouble("budget") ?: 0.0 }
                    
                    _workerStats.value = WorkerStats(earnings, done, 4.8)
                }
            }
    }
}
