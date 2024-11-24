package com.icsa.campus_connect.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.icsa.campus_connect.R
import com.icsa.campus_connect.repository.Event
import java.io.ByteArrayOutputStream

class CreateEventActivity : AppCompatActivity() {

    private lateinit var eventTitleEditText: EditText
    private lateinit var eventDescriptionEditText: EditText
    private lateinit var eventDateEditText: EditText
    private lateinit var rsvpLinkEditText: EditText
    private lateinit var eventPosterImageView: ImageView
    private lateinit var createEventButton: Button
    private lateinit var selectPosterButton: Button
    private var selectedPoster: ByteArray? = null

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference.child("events")
    private val storage = FirebaseStorage.getInstance().reference.child("eventPosters")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        // Initialize views
        eventTitleEditText = findViewById(R.id.eventNameEditText)
        eventDescriptionEditText = findViewById(R.id.eventDescriptionEditText)
        eventDateEditText = findViewById(R.id.eventDateEditText)
        rsvpLinkEditText = findViewById(R.id.rsvpLinkEditText)
        eventPosterImageView = findViewById(R.id.eventPosterImageView)
        createEventButton = findViewById(R.id.createEventButton)
        selectPosterButton = findViewById(R.id.selectPosterButton)

        // Select Poster Button Logic
        selectPosterButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_SELECT_POSTER)
        }

        // Set up create button click listener
        createEventButton.setOnClickListener {
            createEvent()
        }
    }

    private fun createEvent() {
        val eventTitle = eventTitleEditText.text.toString().trim()
        val eventDescription = eventDescriptionEditText.text.toString().trim()
        val eventDate = eventDateEditText.text.toString().trim()
        val rsvpLink = rsvpLinkEditText.text.toString().trim()

        if (eventTitle.isEmpty() || eventDescription.isEmpty() || eventDate.isEmpty() || rsvpLink.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Assuming the logged-in user's ID is available
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique event ID
        val eventId = database.push().key ?: return

        // Create an Event object
        val event = Event(
            eventId = eventId,
            userId = userId,
            eventDate = eventDate,
            eventName = eventTitle,
            eventPoster = "", // Poster URL will be updated after upload
            eventDescription = eventDescription,
            rsvpLink = rsvpLink
        )

        // Save event information to Firebase Realtime Database
        database.child(eventId).setValue(event).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (selectedPoster != null) {
                    uploadEventPoster(eventId)
                } else {
                    Toast.makeText(this, "Event Created Successfully", Toast.LENGTH_SHORT).show()
                    redirectToEventDetail(eventId)
                }
            } else {
                Log.e("CreateEventActivity", "Failed to create event: ${task.exception?.message}")
                Toast.makeText(this, "Failed to create event. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadEventPoster(eventId: String) {
        val photoRef = storage.child("events/$eventId/eventPoster.jpg")

        if (selectedPoster == null || selectedPoster!!.isEmpty()) {
            Log.e("FirebasePhoto", "No image data to upload or image data is empty.")
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show()
            return
        }

        photoRef.putBytes(selectedPoster!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        database.child(eventId).child("eventPoster").setValue(uri.toString())
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Event Created Successfully", Toast.LENGTH_SHORT).show()
                                    redirectToEventDetail(eventId)
                                } else {
                                    Log.e("FirebasePhoto", "Failed to save poster URL to database: ${dbTask.exception?.message}")
                                    Toast.makeText(this, "Failed to save poster URL. Try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }.addOnFailureListener { exception ->
                        Log.e("FirebasePhoto", "Failed to get poster URL: ${exception.message}")
                        Toast.makeText(this, "Failed to get poster URL. Try again.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FirebasePhoto", "Failed to upload event poster: ${task.exception?.message}")
                    Toast.makeText(this, "Failed to upload event poster. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Redirect to EventDetailActivity after successful creation
    private fun redirectToEventDetail(eventId: String) {
        Log.d("CreateEventActivity", "Redirecting to EventDetailActivity for event ID: $eventId")
        val intent = Intent(this, EventDetailActivity::class.java)
        intent.putExtra("eventId", eventId)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_POSTER && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            try {
                val inputStream = contentResolver.openInputStream(imageUri!!)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                eventPosterImageView.setImageBitmap(bitmap) // Display the selected image

                // Compress Bitmap to ByteArray for storing in Firebase Storage
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 50, outputStream)
                selectedPoster = outputStream.toByteArray()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_POSTER = 1
    }
}
