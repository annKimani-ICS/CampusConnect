package com.icsa.campus_connect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.icsa.campus_connect.R
import com.icsa.campus_connect.model.Event

class EventAdapter(
    private val eventList: List<Event> = emptyList(),
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    // ViewHolder class for binding views
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventTitle: TextView = itemView.findViewById(R.id.eventTitle)
        val eventDescription: TextView = itemView.findViewById(R.id.eventDescription)
        val eventDate: TextView = itemView.findViewById(R.id.eventDate)

        init {
            itemView.setOnClickListener {
                val event = eventList[adapterPosition]
                onItemClick(event)
            }
        }
    }

    // Method to create and return ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    // Method to bind event data to the ViewHolder
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.eventTitle.text = event.title
        holder.eventDescription.text = event.description
        holder.eventDate.text = event.date
    }

    // Return the size of the list
    override fun getItemCount(): Int {
        return eventList.size
    }

    // Method to update the list of events and refresh the RecyclerView
    fun submitList(events: List<Event>) {
        (eventList as MutableList).clear()
        eventList.addAll(events)
        notifyDataSetChanged()
    }
}
