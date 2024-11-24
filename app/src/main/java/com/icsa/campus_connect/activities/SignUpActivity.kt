package com.icsa.campus_connect.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.icsa.campus_connect.R
import com.icsa.campus_connect.repository.User
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.security.MessageDigest

class SignUpActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var middleNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var selectPhotoButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var studentRadioButton: RadioButton
    private lateinit var organiserRadioButton: RadioButton
    private var selectedPhoto: ByteArray? = null // Store the photo as a byte array

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference.child("users")
    private val storage = FirebaseStorage.getInstance().reference.child("profilePhotos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize views
        firstNameEditText = findViewById(R.id.firstNameEditText)
        middleNameEditText = findViewById(R.id.middleNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        selectPhotoButton = findViewById(R.id.selectPhotoButton)
        profileImageView = findViewById(R.id.profileImageView)
        studentRadioButton = findViewById(R.id.studentRadioButton)
        organiserRadioButton = findViewById(R.id.organiserRadioButton)

        auth = FirebaseAuth.getInstance()

        // Select Photo Button Logic
        selectPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO)
        }

        // Sign-Up Button Logic
        signUpButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val middleName = middleNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Determine user type
            val userType = when {
                studentRadioButton.isChecked -> "Student"
                organiserRadioButton.isChecked -> "Organiser"
                else -> ""
            }

            if (firstName.isEmpty() || email.isEmpty() || phone.isEmpty() || userType.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Register user
            registerUser(firstName, middleName, lastName, email, phone, userType, password)
        }
    }

    // Hashing function for passwords
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    // Register user using Firebase Authentication
    private fun registerUser(firstName: String, middleName: String, lastName: String, email: String, phone: String, userType: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val hashedPassword = hashPassword(password)
                    saveUserToDatabase(userId, firstName, middleName, lastName, email, phone, userType, hashedPassword)
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Save user information to Firebase Realtime Database
    private fun saveUserToDatabase(
        userId: String,
        firstName: String,
        middleName: String,
        lastName: String,
        email: String,
        phone: String,
        userType: String,
        password: String
    ) {
        val user = User(userId, firstName, middleName, lastName, email, phone, userType, userPassword = password)

        database.child(userId).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // If the profile photo is selected, upload it to Firebase Storage
                if (selectedPhoto != null) {
                    uploadProfilePhoto(userId)
                } else {
                    Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
                    redirectToLogin() // Redirect after successful signup
                }
            } else {
                Toast.makeText(this, "Failed to save user data. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadProfilePhoto(userId: String) {
        val photoRef = storage.child("profilePhotos/users/$userId/profilePhoto.jpg") // Update to match the intended path

        if (selectedPhoto == null || selectedPhoto!!.isEmpty()) {
            Log.e("FirebasePhoto", "No image data to upload or image data is empty.")
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show()
            return
        }

        photoRef.putBytes(selectedPhoto!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        database.child(userId).child("profilePhotoUrl").setValue(uri.toString())
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
                                    redirectToLogin()
                                } else {
                                    Log.e("FirebasePhoto", "Failed to save profile photo URL to database: ${dbTask.exception?.message}")
                                    Toast.makeText(this, "Failed to save profile photo URL. Try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }.addOnFailureListener { exception ->
                        Log.e("FirebasePhoto", "Failed to get photo URL: ${exception.message}")
                        Toast.makeText(this, "Failed to get photo URL. Try again.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FirebasePhoto", "Failed to upload profile photo: ${task.exception?.message}")
                    Toast.makeText(this, "Failed to upload profile photo. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    class SignUpActivity : AppCompatActivity() {

        companion object {
            private const val REQUEST_CODE_SELECT_PHOTO = 1
            private const val REQUEST_CODE_STORAGE_PERMISSION = 2
        }

        // Rest of your class code

        private fun checkStoragePermissions() {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE_PERMISSION)
            }
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Storage permission is required to upload profile photo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // Redirect to LoginActivity after successful registration
    private fun redirectToLogin() {
        Toast.makeText(this, "Redirecting to login...", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                profileImageView.setImageBitmap(bitmap) // Display the selected image

                // Compress Bitmap to ByteArray for storing in Firebase Storage
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream) // Compress to 50% quality
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
