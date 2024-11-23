package com.icsa.campus_connect.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.icsa.campus_connect.R
import com.icsa.campus_connect.repository.User

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        emailEditText = findViewById(R.id.emailInput)
        passwordEditText = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set login button click listener
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    // Login user using Firebase Authentication
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful, get user info from Realtime Database
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val database = FirebaseDatabase.getInstance().getReference("users")

                    // Fetch user's additional data
                    database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user = dataSnapshot.getValue(User::class.java)
                            if (user != null) {
                                // Login successful, proceed to ProfileActivity
                                Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
                                intent.putExtra("userId", userId) // Pass userId to ProfileActivity
                                startActivity(intent)
                                finish() // Close login activity
                            } else {
                                Toast.makeText(this@LoginActivity, "User data not found", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(this@LoginActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    // Login failed
                    Toast.makeText(this, "Invalid email or password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
