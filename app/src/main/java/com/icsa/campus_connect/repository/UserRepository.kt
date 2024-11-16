package com.icsa.campus_connect.repository

import com.icsa.campus_connect.database.DatabaseHelper
import android.content.ContentValues
import android.content.Context
import android.util.Log
import java.security.MessageDigest

data class User(
    val userId: Int = 0,
    val userName: String,
    val userEmail: String,
    val userPhone: String,
    val userType: String,
    val profilePhoto: ByteArray? = null,
    val userPassword: String
)

class UserRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private var loggedInUser: User? = null  // To track the logged-in user

    fun registerUser(user: User): Long {
        val db = dbHelper.writableDatabase

        val contentValues = ContentValues().apply {
            put("user_name", user.userName)
            put("user_email", user.userEmail)
            put("user_phone", user.userPhone)
            put("user_type", user.userType)
            put("profile_photo", user.profilePhoto) // Can be null if no photo is uploaded
            put("user_password", user.userPassword)
        }

        // Insert the new user into the database
        val result = db.insert("Users", null, contentValues)
        db.close() // Close the database connection

        return result // Returns the row ID if successful, -1 if failed
    }

    // Get all users from the database
    fun getAllUsers(): List<User> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("Users", null, null, null, null, null, null)
        val users = mutableListOf<User>()

        with(cursor) {
            while (moveToNext()) {
                users.add(
                    User(
                        userId = getInt(getColumnIndexOrThrow("user_id")),
                        userName = getString(getColumnIndexOrThrow("user_name")),
                        userEmail = getString(getColumnIndexOrThrow("user_email")),
                        userPhone = getString(getColumnIndexOrThrow("user_phone")),
                        userType = getString(getColumnIndexOrThrow("user_type")),
                        profilePhoto = getBlob(getColumnIndexOrThrow("profile_photo")),
                        userPassword = getString(getColumnIndexOrThrow("user_password"))
                    )
                )
            }
        }
        cursor.close()
        return users
    }

    // Hash password using SHA-256
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    // User login logic
    fun loginUser(email: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val hashedPassword = hashPassword(password)
        val query = "SELECT * FROM Users WHERE user_email = ? AND user_password = ?"
        val cursor = db.rawQuery(query, arrayOf(email, hashedPassword))
        Log.d("LoginDebug", "Email: $email, Password: $password")

        return if (cursor.moveToFirst()) {
            val user = User(
                userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                userName = cursor.getString(cursor.getColumnIndexOrThrow("user_name")),
                userEmail = cursor.getString(cursor.getColumnIndexOrThrow("user_email")),
                userPhone = cursor.getString(cursor.getColumnIndexOrThrow("user_phone")),
                userType = cursor.getString(cursor.getColumnIndexOrThrow("user_type")),
                profilePhoto = cursor.getBlob(cursor.getColumnIndexOrThrow("profile_photo")),
                userPassword = cursor.getString(cursor.getColumnIndexOrThrow("user_password"))
            )
            loggedInUser = user  // Set the logged-in user
            user
        } else {
            null // No user found
        }.also {
            cursor.close()
        }
    }

    // Get profile of the logged-in user
    fun getUserProfile(): User? {
        return loggedInUser  // Return the logged-in user profile if logged in
    }
}
