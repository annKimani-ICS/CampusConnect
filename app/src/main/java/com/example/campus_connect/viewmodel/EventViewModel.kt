package com.example.campus_connect.viewmodel

import androidx.lifecycle.ViewModel
import com.example.campus_connect.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class EventViewModel : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    open val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _filteredEvents = MutableStateFlow<List<Event>>(emptyList())
    val filteredEvents: StateFlow<List<Event>> get() = _filteredEvents

    init {
        // Initialize filteredEvents to show all events by default
        updateFilteredEvents()
    }

    fun addEvent(event: Event) {
        _events.update { currentEvents -> currentEvents + event }
        updateFilteredEvents()
    }

    fun deleteEvent(eventId: String) {
        _events.update { currentEvents ->
            currentEvents.filter { it.id != eventId }
        }
        updateFilteredEvents()
    }

    fun updateEvent(updatedEvent: Event) {
        _events.update { currentEvents ->
            currentEvents.map {
                if (it.id == updatedEvent.id) updatedEvent else it
            }
        }
        updateFilteredEvents()
    }

    fun filterEventsByCategory(category: String) {
        _filteredEvents.value = if (category == "Other") {
            _events.value.filter { it.tags.isEmpty() }
        } else {
            _events.value.filter { it.tags.contains(category) }
        }
    }

    private fun updateFilteredEvents() {
        // By default, show all events
        _filteredEvents.value = _events.value
    }
}
