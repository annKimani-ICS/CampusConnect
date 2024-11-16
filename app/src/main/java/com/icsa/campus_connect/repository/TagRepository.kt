package com.icsa.campus_connect.repository

import android.content.ContentValues
import android.content.Context
import com.icsa.campus_connect.database.DatabaseHelper

data class Tag(
    val tagId: Int = 0,
    val tagName: String
)

class TagRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addTag(tag: Tag): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("tag_name", tag.tagName)
        }
        return db.insert("Tags", null, values)
    }

    fun getAllTags(): List<Tag> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("Tags", null, null, null, null, null, null)
        val tags = mutableListOf<Tag>()

        with(cursor) {
            while (moveToNext()) {
                tags.add(
                    Tag(
                        tagId = getInt(getColumnIndexOrThrow("tag_id")),
                        tagName = getString(getColumnIndexOrThrow("tag_name"))
                    )
                )
            }
        }
        cursor.close()
        return tags
    }
}
