package com.icsa.campus_connect.ui.theme

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.TimeZone

//1. Extend AppCompactActivity to allow lifecycle management and access to UI components
class CalendarIntegrationTestActivity : AppCompatActivity(){
//    2. Add calendar read and write permissions to AndroidManifest.

//    3. Adding permission checking
    private val PERMISSION_REQ_CODE = 1001

    private  fun hasCalendarPermission(): Boolean{
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PERMISSION_GRANTED
    }

    private fun requestCalendarPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR),
            PERMISSION_REQ_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ_CODE && grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED){
            Toast.makeText(this, "Calendar permission granted. Try adding the event again", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Calendar permissions are required to add an event", Toast.LENGTH_SHORT).show()

        }
    }

    //4. Adding the UI for testing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Root Layout
        val rootLayout = LinearLayout(this)
        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.setPadding(16,16,16,16)

//        Title Input Field
        val titleInput = EditText(this)
        titleInput.hint = "Event Title"
        rootLayout.addView(titleInput)

//        Description Input Field
        val descriptionInput = EditText(this)
        descriptionInput.hint = "Event Description"
        rootLayout.addView(descriptionInput)

//        Location Input Field
        val locationInput = EditText(this)
        descriptionInput.hint = "Event Location"
        rootLayout.addView(locationInput)

//        Add Event Button
        val addEventButton = Button(this)
        descriptionInput.hint = "Event Description"
        rootLayout.addView(addEventButton)

        this.setContentView(rootLayout)

        addEventButton.setOnClickListener{
            if (hasCalendarPermission()){
                val title = titleInput.text.toString()
                val description = descriptionInput.text.toString()
                val location = locationInput.text.toString()
                addEventToCalendar(title, description, location)
            } else{
                requestCalendarPermission()
            }
        }

    }

    private fun addEventToCalendar(title: String, description: String, location: String) {
        try {
//            Define event time to start 1 hour from now and end after 2 hours
            val startTime = System.currentTimeMillis() + 60 * 60 * 1000
            val endTime = startTime + 2 * 60 * 60 * 1000

//            Set up event variables in a ContentValues()
            val eventVariables = ContentValues()
            eventVariables.put(CalendarContract.Events.CALENDAR_ID, 1) // could be improved to query for phone calendar specifically
            eventVariables.put(CalendarContract.Events.TITLE, title)
            eventVariables.put(CalendarContract.Events.DESCRIPTION, description)
            eventVariables.put(CalendarContract.Events.EVENT_LOCATION, location)
            eventVariables.put(CalendarContract.Events.DTSTART, startTime)
            eventVariables.put(CalendarContract.Events.DTEND, endTime)
            eventVariables.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)

//            Insert event into Calendar
            val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, eventVariables)
            if(uri != null){
                val eventId = uri.lastPathSegment?.toLong()
                Toast.makeText(this, "Event added with ID: $eventId", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show()
            }

        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this, "Error adding the event", Toast.LENGTH_SHORT).show()
        }
    }

}