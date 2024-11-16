package com.icsa.campus_connect.repository

import android.content.ContentValues
import android.content.Context
import com.icsa.campus_connect.database.DatabaseHelper

data class EventTag(
    val eventId: Int,
    val tagId: Int
)

class EventTagRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addEventTag(eventTag: EventTag): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("event_id", eventTag.eventId)
            put("tag_id", eventTag.tagId)
        }
        return db.insert("event_tags", null, values)
    }

    fun getAllEventTags(): List<EventTag> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("event_tags", null, null, null, null, null, null)
        val eventTags = mutableListOf<EventTag>()

        with(cursor) {
            while (moveToNext()) {
                eventTags.add(
                    EventTag(
                        eventId = getInt(getColumnIndexOrThrow("event_id")),
                        tagId = getInt(getColumnIndexOrThrow("tag_id"))
                    )
                )
            }
        }
        cursor.close()
        return eventTags
    }
}
