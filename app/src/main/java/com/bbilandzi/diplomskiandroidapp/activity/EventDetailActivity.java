package com.bbilandzi.diplomskiandroidapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.bbilandzi.diplomskiandroidapp.utils.DateTimeUtil;
import com.bbilandzi.diplomskiandroidapp.viewmodel.EventViewModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
        dateTextView.setText(DateTimeUtil.getIsoIntoMinutesHourDayMonthYear(event.getDate()));
        locationTextView.setText(event.getLocation());
        detailsTextView.setText(event.getDetails());
        creatorTextView.setText("Creator: " + event.getCreator());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.comment_filters, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commentFilterSpinner.setAdapter(adapter);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(new CommentAdapter());

        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        commentAdapter = new CommentAdapter();
        commentsRecyclerView.setAdapter(commentAdapter);
        eventViewModel.getCommentsLiveData().observe(this, commentDTOS -> commentAdapter.updateComments(commentDTOS));

        eventViewModel.getCommentsForEvent(event.getId());

        submitCommentButton.setOnClickListener(v -> {
            String newCommentText = newCommentEditText.getText().toString().trim();
            if (!newCommentText.isEmpty()) {
                CommentDTO newComment = CommentDTO.builder()
                        .commentBody(newCommentText)
                        .eventId(event.getId())
                        .creatorId(AuthUtils.getUserId(this))
                        .creator(AuthUtils.getUsername(this))
                        .creationDate(Instant.now().toString().replaceAll("Z$",""))
                        .build();
                eventViewModel.createCommentForEvent(event.getId(), newComment);
                newCommentEditText.setText("");
            }
        });

        commentFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                List<CommentDTO> currentComments =  eventViewModel.getCommentsLiveData().getValue();
                switch (selectedItem) {
                    case "Oldest first":
                        currentComments.sort(DateTimeUtil.commentDateComparatorReverse());
                        eventViewModel.setOldestFirstFilter(true);
                        break;
                    case "Newest first":
                        currentComments.sort(DateTimeUtil.commentDateComparator());
                        eventViewModel.setOldestFirstFilter(false);
                        break;
                    default:
                        break;
                }
                commentAdapter.updateComments(currentComments);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
                eventViewModel.setOldestFirstFilter(false);
            }
        });
    }
}
