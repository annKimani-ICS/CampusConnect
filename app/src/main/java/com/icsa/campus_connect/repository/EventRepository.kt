package com.icsa.campus_connect.repository

import com.icsa.campus_connect.database.DatabaseHelper
import android.content.ContentValues
import android.content.Context

data class Event(
    val eventId: Int = 0,
    val userId: Int,
    val eventDate: String,
    val eventName: String,
    val eventPoster: ByteArray,
    val eventDescription: String,
    val rsvpLink: String
)

class EventRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addEvent(event: Event): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", event.userId)
            put("event_date", event.eventDate)
            put("event_name", event.eventName)
            put("event_poster", event.eventPoster)
            put("event_description", event.eventDescription)
            put("rsvp_link", event.rsvpLink)
        }
        return db.insert("Events", null, values)
    }

    fun getAllEvents(): List<Event> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("Events", null, null, null, null, null, null)
        val events = mutableListOf<Event>()

        with(cursor) {
            while (moveToNext()) {
                events.add(
                    Event(
                        eventId = getInt(getColumnIndexOrThrow("event_id")),
                        userId = getInt(getColumnIndexOrThrow("user_id")),
                        eventDate = getString(getColumnIndexOrThrow("event_date")),
                        eventName = getString(getColumnIndexOrThrow("event_name")),
                        eventPoster = getBlob(getColumnIndexOrThrow("event_poster")),
                        eventDescription = getString(getColumnIndexOrThrow("event_description")),
                        rsvpLink = getString(getColumnIndexOrThrow("rsvp_link"))
                    )
                )
            }
        }
        cursor.close()
        return events
    }
}
