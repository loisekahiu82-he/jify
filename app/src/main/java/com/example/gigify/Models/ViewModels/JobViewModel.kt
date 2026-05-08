package com.example.gigify.Models.ViewModels

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gigify.Models.Job
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class JobViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
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
            .whereEqualTo("clientId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                val jobList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Job::class.java)
                } ?: emptyList()
                
                _jobs.value = jobList.sortedByDescending { it.createdAt }
            }
    }

    fun postJob(
        title: String, 
        category: String, 
        description: String, 
        budget: String, 
        imageUri: Uri?,
        context: Context, 
        onSuccess: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return
        
        if (title.isEmpty() || category.isEmpty() || description.isEmpty() || budget.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
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

                // Save initial job data immediately for instant feel
                db.collection("Jobs").document(jobId).set(newJob)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Uploading job...", Toast.LENGTH_SHORT).show()
                    onSuccess(jobId)
                }

                // Handle optional image upload in the background
                if (imageUri != null) {
                    val imageRef = storage.reference.child("job_images/${UUID.randomUUID()}")
                    imageRef.putFile(imageUri).await()
                    val downloadUrl = imageRef.downloadUrl.await().toString()
                    
                    // Update the job with the image URL
                    db.collection("Jobs").document(jobId).update("jobImage", downloadUrl).await()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateJob(jobId: String, updates: Map<String, Any>, context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("Jobs").document(jobId).update(updates).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Job updated successfully", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun cancelJob(jobId: String, context: Context) {
        db.collection("Jobs").document(jobId).update("status", "cancelled")
    }

    fun deleteJob(jobId: String, context: Context) {
        db.collection("Jobs").document(jobId).delete()
    }

    fun getJobDetails(jobId: String) {
        db.collection("Jobs").document(jobId).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                _currentJob.value = snapshot.toObject(Job::class.java)
            }
        }
    }
}
