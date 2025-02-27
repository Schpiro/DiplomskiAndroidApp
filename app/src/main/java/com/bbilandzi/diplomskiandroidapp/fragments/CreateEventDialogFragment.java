package com.bbilandzi.diplomskiandroidapp.fragments;

import static com.bbilandzi.diplomskiandroidapp.utils.DateTimeUtil.getDateInMillis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.model.EventDTO;
import com.bbilandzi.diplomskiandroidapp.utils.AuthUtils;
import com.bbilandzi.diplomskiandroidapp.viewmodel.EventViewModel;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateEventDialogFragment extends DialogFragment {
    private EventViewModel eventViewModel;
    private EditText titleEditText;
    private EditText locationEditText;
    private DatePicker datePicker;
    private EditText detailsEditText;
    private Button createButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_event, container, false);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        titleEditText = view.findViewById(R.id.title_edit_text);
        locationEditText = view.findViewById(R.id.location_edit_text);
        datePicker = view.findViewById(R.id.date_picker);
        detailsEditText = view.findViewById(R.id.details_edit_text);
        createButton = view.findViewById(R.id.create_button);

        createButton.setOnClickListener(v -> createEvent());

        return view;
    }

    private void createEvent() {
        String title = titleEditText.getText().toString();
        String location = locationEditText.getText().toString();
        long dateInMillis = getDateInMillis(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        String details = detailsEditText.getText().toString();
        EventDTO eventDTO = EventDTO.builder()
                .title(title)
                .location(location)
                .date(String.valueOf(dateInMillis))
                .details(details)
                .creatorId(AuthUtils.getUserId(requireContext()))
                .build();
        eventViewModel.createEvent(eventDTO);
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Objects.requireNonNull(getDialog().getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }
}
