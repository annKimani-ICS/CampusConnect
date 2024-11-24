package com.icsa.campus_connect.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.icsa.campus_connect.R
import com.icsa.campus_connect.adapter.EventAdapter
import com.icsa.campus_connect.repository.EventRepository

class EventListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var addEventButton: Button
    private lateinit var eventRepository: EventRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        Log.d("EventListActivity", "onCreate: Initializing views and setting up RecyclerView.")

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView)
        addEventButton = findViewById(R.id.addEventButton)

        // Initialize EventRepository
        eventRepository = EventRepository(this)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter { event ->
            Log.d("EventListActivity", "onCreate: Event clicked - \${event.eventName}")
            val intent = Intent(this, EventDetailActivity::class.java)
            //intent.putExtra("eventId", event.eventId)
            startActivity(intent)
        }
        recyclerView.adapter = eventAdapter

        // Set up button click listener
        addEventButton.setOnClickListener {
            Log.d("EventListActivity", "addEventButton: Navigating to CreateEventActivity.")
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent)
        }

        // Fetch events from database
        fetchEventsFromDatabase()
    }

    private fun fetchEventsFromDatabase() {
        Log.d("EventListActivity", "fetchEventsFromDatabase: Fetching events from local database.")
        val events = eventRepository.getAllEvents()
        if (events.isNotEmpty()) {
            eventAdapter.submitList(events)
            Log.d("EventListActivity", "fetchEventsFromDatabase: Events fetched successfully.")
        } else {
            Log.w("EventListActivity", "fetchEventsFromDatabase: No events found.")
            Toast.makeText(this, "No events found.", Toast.LENGTH_SHORT).show()
        }
    }
}
