package com.icsa.campus_connect.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.icsa.campus_connect.model.Event
import com.icsa.campus_connect.viewmodel.EventViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@Composable
fun EventListPage(
    viewModel: EventViewModel,
    onEditEvent: (Event) -> Unit,
    onAddEvent: () -> Unit,
    navController: NavController
) {
    val events by viewModel.events.collectAsState(initial = emptyList())

    // Check if no events exist and navigate to the Create Event page
    if (events.isEmpty()) {
        // Redirect to the Create Event page
        navController.navigate("create_event")
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEvent
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Event"
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (events.isEmpty()) {
                Text(
                    text = "No events found. Please add an event.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(events) { event ->
                        EventCard(event = event, onClick = { onEditEvent(event) })
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Date: ${event.date}",
                style = MaterialTheme.typography.bodySmall
            )
            // Displaying tags as plain text
            if (event.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tags: ${event.tags.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventListPagePreview() {
    // Sample mock data
    val mockEvents = listOf(
        Event(
            id = "1",
            title = "Sample Event 1",
            imageUrl = "null",
            description = "This is a description for the first sample event.",
            date = "2024-11-25",
            link = "ee",
            tags = listOf("Science", "Workshop")
        ),
        Event(
            id = "2",
            title = "Sample Event 2",
            description = "This is a description for the second sample event.",
            date = "2024-12-01",
            link = "e",
            imageUrl = "kk",
            tags = listOf("Technology", "Lecture")
        )
    )

    // Mock EventViewModel with proper StateFlow
    val mockViewModel = object : EventViewModel() {
        override val events: StateFlow<List<Event>> = MutableStateFlow(mockEvents)
    }

    EventListPage(
        viewModel = EventViewModel(),
        onEditEvent = { /* Do nothing */ },
        onAddEvent = { /* Do nothing */ },
        navController = rememberNavController() // Mock NavController for preview
    )
}

@Composable
fun EventListItem(
    event: Event,
    onEditEvent: (Event) -> Unit,
    onDeleteEvent: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Date: ${event.date}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row {
                TextButton(onClick = { onEditEvent(event) }) {
                    Text("Edit")
                }
                TextButton(onClick = onDeleteEvent) {
                    Text("Delete")
                }
            }
        }
    }
}




