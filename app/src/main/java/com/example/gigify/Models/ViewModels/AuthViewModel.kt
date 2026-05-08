package com.example.gigify.Models.ViewModels

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.gigify.Models.User
import com.example.gigify.Navigation.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
            Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
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

                // 1. Save to Firestore
                db.collection("Users").document(uid).set(userData).await()

                withContext(Dispatchers.Main) {
                    // 2. Manually update local state so details are visible INSTANTLY
                    _currentUserData.value = userData
                    _authState.value = AuthState.Success
                    
                    Toast.makeText(context, "Welcome to Gigify, $name!", Toast.LENGTH_SHORT).show()
                    
                    // 3. Navigate directly to dashboard
                    val destination = if (role.lowercase() == "worker") {
                        ROUTE_WORKER_DASHBOARD
                    } else {
                        ROUTE_CLIENT_DASHBOARD
                    }
                    
                    navController.navigate(destination) {
                        popUpTo(ROUTE_REGISTER) { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _authState.value = AuthState.Error(e.message ?: "Registration failed")
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun loginUser(email: String, pass: String, navController: NavController, context: Context) {
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(context, "Email and password required", Toast.LENGTH_SHORT).show()
            return
        }

        _authState.value = AuthState.Loading
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Sign in
                auth.signInWithEmailAndPassword(email, pass).await()
                val uid = auth.currentUser?.uid ?: ""
                
                // 2. Fetch profile
                val snapshot = db.collection("Users").document(uid).get().await()
                val user = snapshot.toObject(User::class.java)

                withContext(Dispatchers.Main) {
                    // 3. Update local state
                    _currentUserData.value = user
                    _authState.value = AuthState.Success

                    val destination = when (user?.role?.lowercase()) {
                        "worker" -> ROUTE_WORKER_DASHBOARD
                        "client" -> ROUTE_CLIENT_DASHBOARD
                        else -> ROUTE_HOME
                    }
                    
                    navController.navigate(destination) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _authState.value = AuthState.Error(e.message ?: "Login failed")
                    Toast.makeText(context, "Invalid credentials or network error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun uploadProfilePicture(uri: Uri, context: Context) {
        val uid = auth.currentUser?.uid ?: return
        _authState.value = AuthState.Loading
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ref = storage.reference.child("profile_pictures/$uid")
                ref.putFile(uri).await()
                val downloadUrl = ref.downloadUrl.await().toString()
                
                db.collection("Users").document(uid).update("profilePhoto", downloadUrl).await()
                
                withContext(Dispatchers.Main) {
                    _authState.value = AuthState.Success
                    Toast.makeText(context, "Profile picture updated", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _authState.value = AuthState.Error(e.message ?: "Upload failed")
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun updateProfile(name: String, phone: String, location: String, context: Context, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        _authState.value = AuthState.Loading
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updates = mapOf(
                    "name" to name,
                    "phone" to phone,
                    "locationName" to location
                )
                
                db.collection("Users").document(uid).update(updates).await()
                
                withContext(Dispatchers.Main) {
                    _authState.value = AuthState.Success
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _authState.value = AuthState.Error(e.message ?: "Update failed")
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun logout(navController: NavController) {
        auth.signOut()
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(0)
        }
    }
}
