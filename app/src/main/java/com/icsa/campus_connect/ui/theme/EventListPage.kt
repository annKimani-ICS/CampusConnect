package com.icsa.campus_connect.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.icsa.campus_connect.R
import com.icsa.campus_connect.adapter.EventAdapter
import com.icsa.campus_connect.model.Event

class EventListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var addEventButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        Log.d("EventListActivity", "onCreate: Initializing views and setting up RecyclerView.")

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView)
        addEventButton = findViewById(R.id.addEventButton)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter { event ->
            Log.d("EventListActivity", "onCreate: Event clicked - ${event.title}")
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("eventId", event.id)
            startActivity(intent)
        }
        recyclerView.adapter = eventAdapter

        // Set up button click listener
        addEventButton.setOnClickListener {
            Log.d("EventListActivity", "addEventButton: Navigating to CreateEventActivity.")
            val intent = Intent(this, CreateEventActivity::class.java)
            startActivity(intent)
        }

        // Fetch events from Firebase
        fetchEventsFromFirebase()
    }

    private fun fetchEventsFromFirebase() {
        Log.d("EventListActivity", "fetchEventsFromFirebase: Fetching events from Firebase.")
        val database = FirebaseDatabase.getInstance().getReference("events")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("EventListActivity", "onDataChange: Data snapshot received from Firebase.")
                val events = mutableListOf<Event>()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    if (event != null) {
                        events.add(event)
                        Log.d("EventListActivity", "onDataChange: Event added - ${event.title}")
                    } else {
                        Log.w("EventListActivity", "onDataChange: Event is null for snapshot - ${eventSnapshot.key}")
                    }
                }
                eventAdapter.submitList(events)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("EventListActivity", "onCancelled: Error fetching events - ${error.message}")
                Toast.makeText(this@EventListActivity, "Failed to load events.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
