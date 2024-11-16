package com.icsa.campus_connect.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.icsa.campus_connect.R
import com.icsa.campus_connect.repository.UserRepository
import com.icsa.campus_connect.repository.User
import java.io.ByteArrayOutputStream
import java.io.InputStream

class SignUpActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var userTypeEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var selectPhotoButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var userRepository: UserRepository
    private var selectedPhoto: ByteArray? = null // Store the photo as a byte array

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        userTypeEditText = findViewById(R.id.userTypeEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        selectPhotoButton = findViewById(R.id.selectPhotoButton)
        profileImageView = findViewById(R.id.profileImageView)

        userRepository = UserRepository(this)

        // Select Photo Button Logic
        selectPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO)
        }

        // Sign-Up Button Logic
        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val userType = userTypeEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || userType.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save user to database
            val user = User(0, name, email, phone, userType, selectedPhoto, password)
            val result = userRepository.registerUser(user)

            if (result > 0) {
                Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
                finish() // Return to previous activity (e.g., LoginActivity)
            } else {
                Toast.makeText(this, "Sign-Up Failed. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                profileImageView.setImageBitmap(bitmap) // Display the selected image

                // Convert Bitmap to ByteArray for storing in the database
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                selectedPhoto = outputStream.toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_PHOTO = 1
    }
}
