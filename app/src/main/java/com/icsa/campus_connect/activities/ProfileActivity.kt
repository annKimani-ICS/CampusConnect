package com.icsa.campus_connect.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.campus_connect.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.icsa.campus_connect.MainActivity
import com.icsa.campus_connect.repository.User

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Get the current logged-in user's ID from FirebaseAuth
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            // If the user is null, show an error and return to login
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Reference to the users node in Firebase Realtime Database
        val database = FirebaseDatabase.getInstance().getReference("users")

        // Fetch user data from Firebase
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    // Update UI with user information
                    findViewById<TextView>(R.id.profileFirstName).text = user.firstName
                    findViewById<TextView>(R.id.profileMiddleName).text = user.middleName
                    findViewById<TextView>(R.id.profileLastName).text = user.lastName
                    findViewById<TextView>(R.id.profileEmail).text = user.userEmail
                    findViewById<TextView>(R.id.profilePhone).text = user.userPhone
                    findViewById<TextView>(R.id.profileUserType).text = user.userType

                    // Load profile photo using Picasso or set a default
                    val profileImageView = findViewById<ImageView>(R.id.profilePicture)
//                    if (user.profilePhotoUrl.isNotEmpty()) {
//                        Picasso.get().load(user.profilePhotoUrl).into(profileImageView)
//                    } else {
//                        profileImageView.setImageResource(R.drawable.default_profile_image)
//                    }
                } else {
                    // Handle the case where no user data is found
                    Toast.makeText(this@ProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Error fetching user data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Set up the logout button
        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
