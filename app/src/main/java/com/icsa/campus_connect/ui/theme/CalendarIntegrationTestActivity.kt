package com.icsa.campus_connect.ui.theme

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

//1. Extend AppCompactActivity to allow lifecycle management and access to UI components
class CalendarIntegrationTestActivity() : AppCompatActivity(){
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
        if (requestCode == PERMISSION_REQ_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Calendar permission granted. Try adding the event again", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Calendar permissions are required to add an event", Toast.LENGTH_SHORT).show()

        }
    }

}