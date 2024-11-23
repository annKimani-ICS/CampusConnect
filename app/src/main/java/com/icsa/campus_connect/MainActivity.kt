package com.icsa.campus_connect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.icsa.campus_connect.activities.LoginActivity
import com.icsa.campus_connect.activities.SignUpActivity

class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            // Initialize Firebase Database
            FirebaseDatabase.getInstance()

            // Find views by their IDs
            loginButton = findViewById(R.id.loginButton)
            signUpButton = findViewById(R.id.signUpButton)

            // Set onClickListener to navigate to LoginActivity
            loginButton.setOnClickListener {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            }

            // Set onClickListener to navigate to SignUpActivity
            signUpButton.setOnClickListener {
                val signUpIntent = Intent(this, SignUpActivity::class.java)
                startActivity(signUpIntent)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing Firebase or UI components", Toast.LENGTH_LONG).show()
        }
    }
}
