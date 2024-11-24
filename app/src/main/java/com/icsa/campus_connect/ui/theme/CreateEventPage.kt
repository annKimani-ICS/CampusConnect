package com.icsa.campus_connect.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.icsa.campus_connect.model.Event
import com.icsa.campus_connect.viewmodel.EventViewModel


import androidx.compose.material3.*


import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape

@Composable
fun CreateEventPage(
    viewModel: EventViewModel,
    initialEvent: Event? = null,
    onEventCreated: () -> Unit
) {
    var title by remember { mutableStateOf(initialEvent?.title ?: "") }
    var description by remember { mutableStateOf(initialEvent?.description ?: "") }
    var date by remember { mutableStateOf(initialEvent?.date ?: "") }
    var link by remember { mutableStateOf(initialEvent?.link ?: "") }
    var tags by remember { mutableStateOf(initialEvent?.tags ?: emptyList()) } // Track tags
    var tagInput by remember { mutableStateOf("") } // Input for adding tags

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Input fields for event details
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )
        TextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date") }
        )
        TextField(
            value = link,
            onValueChange = { link = it },
            label = { Text("Link") }
        )

        // Tag input and display
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = tagInput,
                onValueChange = { tagInput = it },
                label = { Text("Add Tag") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                if (tagInput.isNotBlank()) {
                    tags = tags + tagInput.trim() // Add tag to the list
                    tagInput = "" // Clear input field
                }
            }) {
                Text("Add")
            }
        }

        // Display added tags
        LazyRow(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tags) { tag ->
                AssistChip(
                    onClick = { /* Optional: Tag click action */ },
                    label = { Text(tag) },
                    shape = CircleShape,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        // Save button
        Button(
            onClick = {
                val eventToSave = if (initialEvent != null) {
                    // Update existing event
                    initialEvent.copy(
                        title = title,
                        description = description,
                        date = date,
                        link = link,
                        tags = tags
                    )
                } else {
                    // Create new event
                    Event(
                        title = title,
                        description = description,
                        date = date,
                        link = link,
                        imageUrl = "example",
                        tags = tags
                    )
                }

                // Save event
                if (initialEvent != null) {
                    viewModel.updateEvent(eventToSave)
                } else {
                    viewModel.addEvent(eventToSave)
                }

                onEventCreated()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Event")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateEventPagePreview() {
    // Mock event data for preview
    val mockEvent = Event(
        title = "Mock Event",
        description = "This is a description of the mock event.",
        date = "2024-11-24",
        imageUrl = "null",
        link = "https://example.com",
        tags = listOf("Music", "Tech")
    )

    // Preview the CreateEventPage with mock data
    CreateEventPage(
        viewModel = EventViewModel(),
        initialEvent = mockEvent,
        onEventCreated = { /* Handle event creation */ }
    )
}
