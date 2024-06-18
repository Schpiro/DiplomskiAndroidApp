package com.bbilandzi.diplomskiandroidapp.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.adapter.CommentAdapter;
import com.bbilandzi.diplomskiandroidapp.model.CommentDTO;
import com.bbilandzi.diplomskiandroidapp.model.EventDTO;
import com.bbilandzi.diplomskiandroidapp.utils.AuthUtils;
import com.bbilandzi.diplomskiandroidapp.viewmodel.EventViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EventDetailActivity extends AppCompatActivity {
    private EventDTO event;
    private EventViewModel eventViewModel;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView locationTextView;
    private TextView detailsTextView;
    private TextView creatorTextView;
    private Spinner commentFilterSpinner;
    private RecyclerView commentsRecyclerView;
    private EditText newCommentEditText;
    private Button submitCommentButton;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        event = getIntent().getSerializableExtra("event", EventDTO.class);

        titleTextView = findViewById(R.id.titleTextView);
        dateTextView = findViewById(R.id.dateTextView);
        locationTextView = findViewById(R.id.locationTextView);
        detailsTextView = findViewById(R.id.detailsTextView);
        creatorTextView = findViewById(R.id.creatorTextView);
        commentFilterSpinner = findViewById(R.id.commentFilterSpinner);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        newCommentEditText = findViewById(R.id.newCommentEditText);
        submitCommentButton = findViewById(R.id.submitCommentButton);

        titleTextView.setText(event.getTitle());
        dateTextView.setText(event.getDate());
        locationTextView.setText(event.getLocation());
        detailsTextView.setText(event.getDetails());
        creatorTextView.setText("Creator: " + event.getCreator());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.comment_filters, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commentFilterSpinner.setAdapter(adapter);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(new CommentAdapter());

        // Initialize ViewModel
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        commentAdapter = new CommentAdapter();
        commentsRecyclerView.setAdapter(commentAdapter);
        // Observe the comments LiveData
        eventViewModel.getCommentsLiveData().observe(this, commentDTOS -> commentAdapter.updateComments(commentDTOS));

        // Load comments for the current event
        eventViewModel.getCommentsForEvent(event.getId());

        submitCommentButton.setOnClickListener(v -> {
            String newCommentText = newCommentEditText.getText().toString().trim();
            if (!newCommentText.isEmpty()) {
                CommentDTO newComment = CommentDTO.builder()
                        .commentBody(newCommentText)
                        .eventId(event.getId())
                        .creatorId(AuthUtils.getUserId(this))
                        .creator(AuthUtils.getUsername(this))
                        .build();
                eventViewModel.createCommentForEvent(event.getId(), newComment);
                newCommentEditText.setText("");
            }
        });
    }
}
