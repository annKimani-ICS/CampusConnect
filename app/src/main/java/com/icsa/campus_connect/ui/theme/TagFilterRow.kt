package com.icsa.campus_connect.ui.theme



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.icsa.campus_connect.model.Event

import androidx.compose.material3.*


@Composable
fun TagFilterRow(
    events: List<Event>,
    selectedTag: String?,
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allTags = events.flatMap { it.tags }.distinct() // Get unique tags from events

    LazyRow(modifier = modifier) {
        items(allTags) { tag ->
            // Display tags as clickable chips
            FilterChip(
                selected = selectedTag == tag, // Highlight the selected tag
                onClick = { onTagSelected(tag) },
                label = { Text(tag) },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TagFilterRowWithFilterPreview() {
    // Sample mock data for events with tags
    val mockEvents = listOf(
        Event(id = "1", title = "Event 1", description = "Description 1", date = "2024-11-25", tags = listOf("Sports", "Art"), imageUrl = "ex", link = "dd.com"),
        Event(id = "2", title = "Event 2", description = "Description 2", date = "2024-11-26", tags = listOf("Music", "Tech"), imageUrl = "ex", link = "dd.com"),
        Event(id = "3", title = "Event 3", description = "Description 3", date = "2024-11-27", tags = listOf("Art", "Tech"), imageUrl = "ex", link = "dd.com"),
        Event(id = "4", title = "Event 4", description = "Description 4", date = "2024-11-28", tags = listOf("Sports", "Music"), imageUrl = "ex", link = "dd.com")
    )

    val selectedTag = remember { mutableStateOf<String?>(null) }

    // Filter events based on selected tag
    val filteredEvents = remember(selectedTag.value) {
        if (selectedTag.value == null) mockEvents else mockEvents.filter { it.tags.contains(selectedTag.value) }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Display the tag filter row
        TagFilterRow(
            events = mockEvents,
            selectedTag = selectedTag.value,
            onTagSelected = { tag ->
                selectedTag.value = if (selectedTag.value == tag) null else tag // Toggle the tag selection
            },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Display filtered events
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredEvents) { event ->
                EventCard(
                    event = event,
                    onClick = { /* Handle click on event card */ }
                )
            }
        }
    }
}



