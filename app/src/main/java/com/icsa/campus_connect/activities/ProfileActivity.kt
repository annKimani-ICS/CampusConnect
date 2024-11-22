package com.icsa.campus_connect.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.icsa.campus_connect.repository.UserRepository
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.icsa.campus_connect.R

class ProfileActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userRepository = UserRepository(this)

        // Fetch the logged-in user's profile
        val user = userRepository.getUserProfile()

        if (user != null) {
            // Display user profile information
            findViewById<TextView>(R.id.profileName).text = user.userName
            findViewById<TextView>(R.id.profileEmail).text = user.userEmail
            findViewById<TextView>(R.id.profilePhone).text = user.userPhone
            findViewById<TextView>(R.id.profileUserType).text = user.userType

            // Handle the profile photo
            val profileImageView = findViewById<ImageView>(R.id.profilePicture)
            if (user.profilePhotoUrl != null) {
                // Convert ByteArray to Bitmap
              //  val profileBitmap: Bitmap = BitmapFactory.decodeByteArray(user.profilePhotoUrl, 0, user.profilePhotoUrl.size)
               // profileImageView.setImageBitmap(profileBitmap)
            } else {
                // Set a default image if profile photo is not available
                profileImageView.setImageResource(R.drawable.default_profile_image)
            }
        } else {
            // Handle the case where no user is logged in
            findViewById<TextView>(R.id.profileName).text = "No user logged in"
            findViewById<TextView>(R.id.profileEmail).text = ""
            findViewById<TextView>(R.id.profilePhone).text = ""
            findViewById<TextView>(R.id.profileUserType).text = ""

            // Set a default image when no user is logged in
            val profileImageView = findViewById<ImageView>(R.id.profilePicture)
            profileImageView.setImageResource(R.drawable.default_profile_image)
        }
    }
}
