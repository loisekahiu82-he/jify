package com.example.gigify.Models.ViewModels

import androidx.lifecycle.ViewModel
import com.example.gigify.Data.Worker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _workers = MutableStateFlow<List<Worker>>(emptyList())
    val workers: StateFlow<List<Worker>> = _workers

    private val _categories = MutableStateFlow<List<String>>(
        listOf(
            "All", "Fundi", "Cleaner", "Driver", "Painter",
            "Electrician", "Carpenter", "Gardener", "Mechanic", "Barber", "Nanny"
        )
    )
    val categories: StateFlow<List<String>> = _categories

    init {
        fetchWorkers()
    }

    fun fetchWorkers() {
        db.collection("Workers").addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            
            val workerList = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Worker::class.java)
            } ?: emptyList()
            
            if (workerList.isEmpty()) {
                loadMockWorkers()
            } else {
                _workers.value = workerList
            }
        }
    }

    private fun loadMockWorkers() {
        _workers.value = listOf(
            Worker(userId = "1", profession = "Plumber", rating = 4.8, reviewCount = 42),
            Worker(userId = "2", profession = "House cleaner", rating = 4.9, reviewCount = 87),
            Worker(userId = "3", profession = "Painter", rating = 4.6, reviewCount = 29),
            Worker(userId = "4", profession = "Electrician", rating = 4.7, reviewCount = 56),
            Worker(userId = "5", profession = "Carpenter", rating = 4.5, reviewCount = 34),
            Worker(userId = "6", profession = "Gardener", rating = 4.9, reviewCount = 12),
            Worker(userId = "7", profession = "Mechanic", rating = 4.3, reviewCount = 89),
            Worker(userId = "8", profession = "Barber", rating = 4.8, reviewCount = 150),
            Worker(userId = "9", profession = "Nanny", rating = 4.9, reviewCount = 22),
            Worker(userId = "10", profession = "Driver", rating = 4.6, reviewCount = 78)
        )
    }
}
