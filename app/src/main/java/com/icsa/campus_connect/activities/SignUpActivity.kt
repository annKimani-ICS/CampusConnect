package com.icsa.campus_connect.activities

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.icsa.campus_connect.R
import com.icsa.campus_connect.repository.User
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
    private var selectedPhotoUri: Uri? = null // Store the photo URI for upload to Firebase

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference.child("users")
    private val storage = FirebaseStorage.getInstance().reference.child("profilePhotos")

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

        auth = FirebaseAuth.getInstance()

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

            if (selectedPhotoUri == null) {
                Toast.makeText(this, "Please select a profile photo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(name, email, phone, userType, password)
        }
    }

    // Register user using Firebase Authentication
    private fun registerUser(name: String, email: String, phone: String, userType: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    uploadProfilePhoto(userId, name, email, phone, userType)
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Upload profile photo to Firebase Storage
    private fun uploadProfilePhoto(userId: String, name: String, email: String, phone: String, userType: String) {
        val photoRef = storage.child(userId)
        selectedPhotoUri?.let { uri ->
            photoRef.putFile(uri)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Get the download URL and update the user's profile
                        photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            saveUserToDatabase(userId, name, email, phone, userType, downloadUri.toString())
                        }
                    } else {
                        Toast.makeText(this, "Failed to upload profile photo. Try again.", Toast.LENGTH_SHORT).show()
                    }
                }
        } ?: run {
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show()
        }
    }

    // Save user information to Firebase Realtime Database
    private fun saveUserToDatabase(userId: String, name: String, email: String, phone: String, userType: String, profilePhotoUrl: String) {
        val user = User(userId, name, email, phone, userType, profilePhotoUrl)

        database.child(userId).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
                finish() // Close the activity or go to LoginActivity
            } else {
                Toast.makeText(this, "Failed to save user data. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle photo selection result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(selectedPhotoUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                profileImageView.setImageBitmap(bitmap) // Display the selected image
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
