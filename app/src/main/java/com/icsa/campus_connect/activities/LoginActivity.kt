package com.icsa.campus_connect.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.icsa.campus_connect.R
import com.icsa.campus_connect.repository.UserRepository

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailInput)
        passwordEditText = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        userRepository = UserRepository(this)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Attempt login
            val user = userRepository.loginUser(email, password)
            if (user != null) {
                // Login successful
                Toast.makeText(this, "Welcome, ${user.userName}!", Toast.LENGTH_SHORT).show()
                // Redirect to home screen or dashboard
                // Example: startActivity(Intent(this, HomeActivity::class.java))
            } else {
                // Login failed
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
