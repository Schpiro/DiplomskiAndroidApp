package com.bbilandzi.diplomskiandroidapp.activity;

import static com.bbilandzi.diplomskiandroidapp.utils.DateTimeUtil.getDateInMillis;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.adapter.EventAdapter;
import com.bbilandzi.diplomskiandroidapp.fragments.CreateEventDialogFragment;
import com.bbilandzi.diplomskiandroidapp.model.EventDTO;
import com.bbilandzi.diplomskiandroidapp.viewmodel.EventViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import java.util.ArrayList;
import java.util.Calendar;

@AndroidEntryPoint
public class EventListActivity extends BaseActivity {

    private EventAdapter eventAdapter;
    private EventViewModel eventViewModel;
    private String selectedDate;
    private Button createButton;
    private ImageView filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityContent(R.layout.activity_event_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(new ArrayList<>(), this::onEventClick);
        recyclerView.setAdapter(eventAdapter);

        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        eventViewModel.getEventsLiveData().observe(this, eventDTOS -> eventAdapter.updateEvents(eventDTOS));

        filter = findViewById(R.id.selected_date_filter);
        createButton = findViewById(R.id.create_button);
        filter.setOnClickListener(v -> openDatePicker());
        createButton.setOnClickListener(v -> openCreateEventDialog());
        eventViewModel.getAllEvents();
    }

    private void onEventClick(EventDTO event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    filterEventsByDate(getDateInMillis(year1, month1, dayOfMonth));
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void filterEventsByDate(Long date) {
        eventViewModel.filterEvents(date);
    }

    private void openCreateEventDialog() {
        CreateEventDialogFragment dialog = new CreateEventDialogFragment();
        dialog.show(getSupportFragmentManager(), "CreateEventDialogFragment");
    }
}
