package com.example.gigify.Models.ViewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.gigify.Models.Job
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class JobViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs

    private val _currentJob = MutableStateFlow<Job?>(null)
    val currentJob: StateFlow<Job?> = _currentJob

    init {
        fetchJobs()
    }

    fun fetchJobs() {
        val uid = auth.currentUser?.uid ?: return
        
        db.collection("Jobs")
            .whereIn("clientId", listOf(uid))
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                val jobList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Job::class.java)
                } ?: emptyList()
                
                _jobs.value = jobList
            }
    }

    fun postJob(title: String, category: String, description: String, budget: String, context: Context, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        
        if (title.isEmpty() || category.isEmpty() || description.isEmpty() || budget.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val jobId = db.collection("Jobs").document().id
        val newJob = Job(
            jobId = jobId,
            clientId = uid,
            title = title,
            category = category,
            description = description,
            budget = budget.toDoubleOrNull() ?: 0.0,
            status = "pending",
            createdAt = System.currentTimeMillis()
        )

        db.collection("Jobs").document(jobId).set(newJob)
            .addOnSuccessListener {
                Toast.makeText(context, "Job posted successfully", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to post job: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun updateJob(jobId: String, updates: Map<String, Any>, context: Context, onSuccess: () -> Unit) {
        db.collection("Jobs").document(jobId).update(updates)
            .addOnSuccessListener {
                Toast.makeText(context, "Job updated successfully", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun cancelJob(jobId: String, context: Context) {
        db.collection("Jobs").document(jobId).update("status", "cancelled")
            .addOnSuccessListener {
                Toast.makeText(context, "Job cancelled", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to cancel: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    fun deleteJob(jobId: String, context: Context) {
        db.collection("Jobs").document(jobId).delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Job deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun getJobDetails(jobId: String) {
        db.collection("Jobs").document(jobId).get()
            .addOnSuccessListener { doc ->
                _currentJob.value = doc.toObject(Job::class.java)
            }
    }
}
