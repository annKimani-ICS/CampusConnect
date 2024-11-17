package com.icsa.campus_connect

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.icsa.campus_connect.database.DatabaseHelper
import com.icsa.campus_connect.ui.theme.Campus_connectTheme
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.style.TextDirection.Companion.Content
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContent {
            Campus_connectTheme {
                Greeting("Campus Connect") // Update the greeting argument here
            }
        }

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.writableDatabase

        // Check if the database is open
        if (db.isOpen) {
            Log.d("DatabaseTest", "Database opened successfully.")
        } else {
            Log.e("DatabaseTest", "Failed to open database.")
        }

        // Get the database path
        val databaseName = "campus_connect.db"
        val databasePath = getDatabasePath(databaseName)

        Log.d("Database Path", "Database is stored at: $databasePath")

    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Campus_connectTheme {
        Greeting("Campus Connect") // Updated preview message
    }
}

