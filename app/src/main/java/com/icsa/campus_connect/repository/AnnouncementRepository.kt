package com.icsa.campus_connect.repository

import com.icsa.campus_connect.database.DatabaseHelper
import android.content.ContentValues
import android.content.Context

data class Announcement(
    val announcementId: Int = 0,
    val userId: Int,
    val announcementDate: String,
    val announcementName: String,
    val announcementDescription: String
)

class AnnouncementRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addAnnouncement(announcement: Announcement): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", announcement.userId)
            put("announcement_date", announcement.announcementDate)
            put("announcement_name", announcement.announcementName)
            put("announcement_description", announcement.announcementDescription)
        }
        return db.insert("Announcements", null, values)
    }

    fun getAllAnnouncements(): List<Announcement> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("Announcements", null, null, null, null, null, null)
        val announcements = mutableListOf<Announcement>()

        with(cursor) {
            while (moveToNext()) {
                announcements.add(
                    Announcement(
                        announcementId = getInt(getColumnIndexOrThrow("announcement_id")),
                        userId = getInt(getColumnIndexOrThrow("user_id")),
                        announcementDate = getString(getColumnIndexOrThrow("announcement_date")),
                        announcementName = getString(getColumnIndexOrThrow("announcement_name")),
                        announcementDescription = getString(getColumnIndexOrThrow("announcement_description"))
                    )
                )
            }
        }
        cursor.close()
        return announcements
    }
}
