package com.bbilandzi.diplomskiandroidapp.adapter;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.model.EventDTO;
import com.bbilandzi.diplomskiandroidapp.utils.DateTimeUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private static List<EventDTO> eventList;
    private static OnCommentsButtonClickListener commentsButtonClickListener;


    public EventAdapter(List<EventDTO> eventList, OnCommentsButtonClickListener commentsButtonClickListener) {
        this.eventList = eventList;
        this.commentsButtonClickListener = commentsButtonClickListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventDTO event = eventList.get(position);
        holder.bind(event);
        holder.itemView.setOnClickListener(v -> {
            boolean expanded = event.isExpanded(); // Check current state
            event.setExpanded(!expanded); // Toggle the state
            notifyItemChanged(position); // Refresh item
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateEvents(List<EventDTO> newEventList) {
        eventList = newEventList;
        notifyDataSetChanged();
    }

    public interface OnCommentsButtonClickListener {
        void onCommentsButtonClick(EventDTO event);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        private TextView eventTitleTextView;
        private TextView eventDateTextView;
        private TextView eventLocationTextView;
        private LinearLayout layoutDetails;
        private TextView eventCreatorTextView;
        private TextView eventDetailsTextView;
        private ImageButton openCommentsButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitleTextView = itemView.findViewById(R.id.eventTitle);
            eventDateTextView = itemView.findViewById(R.id.eventDate);
            eventLocationTextView = itemView.findViewById(R.id.eventLocation);
            layoutDetails = itemView.findViewById(R.id.layoutDetails);
            eventCreatorTextView = itemView.findViewById(R.id.eventCreator);
            eventDetailsTextView = itemView.findViewById(R.id.eventDetails);
            openCommentsButton = itemView.findViewById(R.id.openCommentsButton);

            // Set click listener for opening comments
            openCommentsButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    EventDTO event = eventList.get(position);
                    commentsButtonClickListener.onCommentsButtonClick(event);
                }
            });
        }

        public void bind(EventDTO event) {
            eventTitleTextView.setText(event.getTitle());
            eventDateTextView.setText(DateTimeUtil.getIsoIntoYearMonthDate(event.getDate()));
            eventLocationTextView.setText(event.getLocation());

            // Toggle visibility of additional details
            if (event.isExpanded()) {
                layoutDetails.setVisibility(View.VISIBLE);
                eventCreatorTextView.setText("Creator: " + event.getCreator());
                eventDetailsTextView.setText("Details: " + event.getDetails());
            } else {
                layoutDetails.setVisibility(View.GONE);
            }
        }
    }

    public interface OnEventClickListener {
        void onEventClick(EventDTO event);
    }
}
