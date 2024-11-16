package com.icsa.campus_connect.repository

import android.content.ContentValues
import android.content.Context
import com.icsa.campus_connect.database.DatabaseHelper

data class StudentTag(
    val userId: Int,
    val tagId: Int
)

class StudentTagRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addStudentTag(studentTag: StudentTag): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", studentTag.userId)
            put("tag_id", studentTag.tagId)
        }
        return db.insert("student_tags", null, values)
    }

    fun getAllStudentTags(): List<StudentTag> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("student_tags", null, null, null, null, null, null)
        val studentTags = mutableListOf<StudentTag>()

        with(cursor) {
            while (moveToNext()) {
                studentTags.add(
                    StudentTag(
                        userId = getInt(getColumnIndexOrThrow("user_id")),
                        tagId = getInt(getColumnIndexOrThrow("tag_id"))
                    )
                )
            }
        }
        cursor.close()
        return studentTags
    }
}
