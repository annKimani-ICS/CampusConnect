package com.icsa.campus_connect.model

import android.graphics.Bitmap
import java.util.UUID

data class Event(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val imageUrl: String,
    val date: String,
    val location: String = "",
    val link: String,
    val tags: List<String> = emptyList() // Add tags to the Event model
)