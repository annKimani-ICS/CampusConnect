package com.icsa.campus_connect.model

import java.util.UUID

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val link: String,
    val imageUrl: String? = null,
    val tags: List<String> = emptyList()
)
