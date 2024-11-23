package com.icsa.campus_connect.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

data class User(
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userType: String = "",
    val profilePhotoUrl: String = "",  // Changed from ByteArray? to String
    val userPassword: String = ""
)

class UserRepository(context: Context) {
    private val database = FirebaseDatabase.getInstance().reference.child("users")
    private val auth = FirebaseAuth.getInstance()
    private var loggedInUser: User? = null // To track the logged-in user

    // Register user using Firebase Authentication
    fun registerUser(userName: String, email: String, phone: String, userType: String, password: String, onComplete: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User registered successfully
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                    val newUser = User(userId = userId, userName = userName, userEmail = email, userPhone = phone, userType = userType)

                    // Save additional user info in Realtime Database
                    database.child(userId).setValue(newUser).addOnCompleteListener { dbTask ->
                        onComplete(dbTask.isSuccessful)
                    }
                } else {
                    Log.e("FirebaseRegister", "Registration failed: ${task.exception?.message}")
                    onComplete(false)
                }
            }
    }

    // User login logic using Firebase Authentication
    fun loginUser(email: String, password: String, onLoginResult: (User?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener

                    // Fetch user profile from Realtime Database
                    database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)
                            loggedInUser = user
                            onLoginResult(user)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("FirebaseLogin", "Login failed: ${error.message}")
                            onLoginResult(null)
                        }
                    })
                } else {
                    Log.e("FirebaseLogin", "Login failed: ${task.exception?.message}")
                    onLoginResult(null)
                }
            }
    }

    // Get profile of the logged-in user
    fun getUserProfile(): User? {
        return loggedInUser // Return the logged-in user's profile if logged in
    }

    // Get all users from Firebase Realtime Database
    fun getAllUsers(onResult: (List<User>) -> Unit) {
        val usersList = mutableListOf<User>()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let { usersList.add(it) }
                }
                onResult(usersList) // Return the list of users
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseGetAllUsers", "Failed to get users: ${databaseError.message}")
                onResult(emptyList()) // Return an empty list in case of error
            }
        })
    }
}
