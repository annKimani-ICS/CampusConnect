package com.icsa.campus_connect.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "campus_connect.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Users Table
        db.execSQL("""
            CREATE TABLE Users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_name VARCHAR(50) NOT NULL,
                user_email VARCHAR(100) NOT NULL UNIQUE,
                user_phone VARCHAR(50) NOT NULL,
                user_type CHAR(10) NOT NULL,
                profile_photo BLOB,
                user_password VARCHAR(50) NOT NULL
            )
        """)

        // Create Events Table
        db.execSQL("""
            CREATE TABLE Events (
                event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                event_date DATETIME NOT NULL,
                event_name VARCHAR(50) NOT NULL,
                event_poster BLOB NOT NULL,
                event_description VARCHAR(200) NOT NULL,
                rsvp_link VARCHAR(50) NOT NULL,
                FOREIGN KEY (user_id) REFERENCES Users(user_id)
            )
        """)

        // Create Announcements Table
        db.execSQL("""
            CREATE TABLE Announcements (
                announcement_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                announcement_date DATETIME NOT NULL,
                announcement_name VARCHAR(50) NOT NULL,
                announcement_description VARCHAR(200) NOT NULL,
                FOREIGN KEY (user_id) REFERENCES Users(user_id)
            )
        """)

        // Create Tags Table
        db.execSQL("""
            CREATE TABLE Tags (
                tag_id INTEGER PRIMARY KEY AUTOINCREMENT,
                tag_name VARCHAR(50) NOT NULL
            )
        """)

        // Create event_tags Table
        db.execSQL("""
            CREATE TABLE event_tags (
                event_id INTEGER NOT NULL,
                tag_id INTEGER NOT NULL,
                PRIMARY KEY (event_id, tag_id),
                FOREIGN KEY (event_id) REFERENCES Events(event_id),
                FOREIGN KEY (tag_id) REFERENCES Tags(tag_id)
            )
        """)

        // Create student_events Table
        db.execSQL("""
            CREATE TABLE student_events (
                user_id INTEGER NOT NULL,
                event_id INTEGER NOT NULL,
                add_calender BOOLEAN NOT NULL,
                PRIMARY KEY (user_id, event_id),
                FOREIGN KEY (user_id) REFERENCES Users(user_id),
                FOREIGN KEY (event_id) REFERENCES Events(event_id)
            )
        """)

        // Create student_tags Table
        db.execSQL("""
            CREATE TABLE student_tags (
                user_id INTEGER NOT NULL,
                tag_id INTEGER NOT NULL,
                PRIMARY KEY (user_id, tag_id),
                FOREIGN KEY (user_id) REFERENCES Users(user_id),
                FOREIGN KEY (tag_id) REFERENCES Tags(tag_id)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS student_tags")
        db.execSQL("DROP TABLE IF EXISTS student_events")
        db.execSQL("DROP TABLE IF EXISTS event_tags")
        db.execSQL("DROP TABLE IF EXISTS Tags")
        db.execSQL("DROP TABLE IF EXISTS Announcements")
        db.execSQL("DROP TABLE IF EXISTS Events")
        db.execSQL("DROP TABLE IF EXISTS Users")
        onCreate(db)
    }
}

