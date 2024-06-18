package com.bbilandzi.diplomskiandroidapp.fragments;

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

import com.bbilandzi.diplomskiandroidapp.R;

public class CreateEventDialogFragment extends DialogFragment {

    private EditText titleEditText;
    private EditText locationEditText;
    private DatePicker datePicker;
    private EditText detailsEditText;
    private Button createButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_event, container, false);

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
        String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
        String details = detailsEditText.getText().toString();
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            // Set the width of the dialog to match the parent
            getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }
}
