package com.icsa.campus_connect

data class User(
    val userId: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val userType: String,
    val picture: ByteArray?,
    val password: String
)
