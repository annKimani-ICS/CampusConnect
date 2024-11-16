package com.icsa.campus_connect.repository

import android.content.ContentValues
import android.content.Context
import com.icsa.campus_connect.database.DatabaseHelper

data class StudentEvent(
    val userId: Int,
    val eventId: Int,
    val addCalendar: Boolean
)

class StudentEventRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addStudentEvent(studentEvent: StudentEvent): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", studentEvent.userId)
            put("event_id", studentEvent.eventId)
            put("add_calender", if (studentEvent.addCalendar) 1 else 0)
        }
        return db.insert("student_events", null, values)
    }

    fun getAllStudentEvents(): List<StudentEvent> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("student_events", null, null, null, null, null, null)
        val studentEvents = mutableListOf<StudentEvent>()

        with(cursor) {
            while (moveToNext()) {
                studentEvents.add(
                    StudentEvent(
                        userId = getInt(getColumnIndexOrThrow("user_id")),
                        eventId = getInt(getColumnIndexOrThrow("event_id")),
                        addCalendar = getInt(getColumnIndexOrThrow("add_calender")) == 1
                    )
                )
            }
        }
        cursor.close()
        return studentEvents
    }
}
