package com.example.gigify.Models.ViewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.gigify.Models.User
import com.example.gigify.Navigation.ROUTE_HOME
import com.example.gigify.Navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUserData = MutableStateFlow<User?>(null)
    val currentUserData: StateFlow<User?> = _currentUserData

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    init {
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                .build()
            db.firestoreSettings = settings
        } catch (e: Exception) {
        }

        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                fetchCurrentUser()
            } else {
                _currentUserData.value = null
            }
        }
    }

    fun fetchCurrentUser() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("Users").document(uid).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                _currentUserData.value = snapshot.toObject(User::class.java)
            }
        }
    }

    fun registerUser(
        name: String, 
        email: String, 
        phone: String, 
        location: String,
        role: String,
        pass: String, 
        navController: NavController, 
        context: Context
    ) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || location.isEmpty() || pass.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        _authState.value = AuthState.Loading
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val uid = result.user?.uid ?: ""
                
                val userData = User(
                    uid = uid,
                    name = name,
                    email = email,
                    phone = phone,
                    locationName = location,
                    role = role.lowercase(),
                    createdAt = System.currentTimeMillis()
                )

                db.collection("Users").document(uid).set(userData)
                    .addOnFailureListener { e -> 
                        Log.e("AuthVM", "Firestore Background Save Failed: ${e.message}")
                    }

                withContext(Dispatchers.Main) {
                    _authState.value = AuthState.Success
                    Toast.makeText(context, "Registration Complete! Please log in.", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _authState.value = AuthState.Error(e.message ?: "Failed")
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun loginUser(email: String, pass: String, navController: NavController, context: Context) {
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    navController.navigate(ROUTE_HOME) {
                        popUpTo(ROUTE_HOME) { inclusive = true }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login Failed")
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun updateProfile(name: String, phone: String, location: String, context: Context, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "name" to name,
            "phone" to phone,
            "locationName" to location
        )
        
        db.collection("Users").document(uid).update(updates)
            .addOnSuccessListener {
                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
    }

    fun uploadProfilePicture(uri: Uri, context: Context) {
        val uid = auth.currentUser?.uid ?: return
        _authState.value = AuthState.Loading
        
        val fileRef = storage.reference.child("profile_pictures/$uid.jpg")
        fileRef.putFile(uri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    db.collection("Users").document(uid).update("profilePhoto", downloadUrl.toString())
                        .addOnSuccessListener {
                            _authState.value = AuthState.Success
                            Toast.makeText(context, "Photo uploaded", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Upload failed")
                Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    fun logout(navController: NavController) {
        auth.signOut()
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(0)
        }
    }
}
