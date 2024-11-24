package com.icsa.campus_connect.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.icsa.campus_connect.R
import com.squareup.picasso.Picasso

class EventDetailActivity : AppCompatActivity() {
    private lateinit var eventTitleTextView: TextView
    private lateinit var eventDescriptionTextView: TextView
    private lateinit var eventDateTextView: TextView
    private lateinit var rsvpLinkTextView: TextView
    private lateinit var eventPosterImageView: ImageView
    private lateinit var logOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        // Initialize views
        eventTitleTextView = findViewById(R.id.eventTitleTextView)
        eventDescriptionTextView = findViewById(R.id.eventDescriptionTextView)
        eventDateTextView = findViewById(R.id.eventDateTextView)
        rsvpLinkTextView = findViewById(R.id.rsvpLinkTextView)
        eventPosterImageView = findViewById(R.id.eventPosterImageView)
        logOutButton = findViewById(R.id.logOutButton)

        // Set up logout button click listener
        logOutButton.setOnClickListener {
            logOutUser()
        }

        // Get event ID from intent
        val eventId = intent.getStringExtra("eventId")

        if (eventId != null) {
            // Fetch event details from Firebase using eventId
            fetchEventDetails(eventId)
        } else {
            Toast.makeText(this, "Event ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchEventDetails(eventId: String) {
        Log.d("EventDetailActivity", "Fetching event details for ID: $eventId")
        val database = FirebaseDatabase.getInstance().getReference("events").child(eventId)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val eventTitle = snapshot.child("eventName").value as? String ?: "N/A"
                    val eventDescription = snapshot.child("eventDescription").value as? String ?: "N/A"
                    val eventDate = snapshot.child("eventDate").value as? String ?: "N/A"
                    val rsvpLink = snapshot.child("rsvpLink").value as? String ?: "N/A"
                    val eventPosterUrl = snapshot.child("eventPosterUrl").value as? String

                    // Set the event details to UI
                    eventTitleTextView.text = eventTitle
                    eventDescriptionTextView.text = eventDescription
                    eventDateTextView.text = "Date: $eventDate"
                    rsvpLinkTextView.text = "RSVP: $rsvpLink"

                    // Load poster using Picasso, if available
                    if (!eventPosterUrl.isNullOrEmpty()) {
                        Picasso.get().load(eventPosterUrl).into(eventPosterImageView)
                    } else {
                        eventPosterImageView.setImageResource(R.drawable.default_profile_image) //remember to change this
                    }
                } else {
                    Toast.makeText(this@EventDetailActivity, "Event not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("EventDetailActivity", "Error fetching event details: ${error.message}")
                Toast.makeText(this@EventDetailActivity, "Failed to load event details.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Log out the current user and navigate back to the login page
    private fun logOutUser() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
