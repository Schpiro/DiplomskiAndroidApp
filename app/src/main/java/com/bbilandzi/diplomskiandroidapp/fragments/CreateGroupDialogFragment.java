package com.bbilandzi.diplomskiandroidapp.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.model.UserDTO;
import com.bbilandzi.diplomskiandroidapp.model.UserGroup;
import com.bbilandzi.diplomskiandroidapp.viewmodel.ContactViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreateGroupDialogFragment extends DialogFragment {
    private ContactViewModel contactViewModel;
    private final LiveData<List<UserDTO>> users;
    private EditText editTextGroupName;
    private List<Long> selectedUserIds = new ArrayList<>();

    public CreateGroupDialogFragment(LiveData<List<UserDTO>> users) {
        this.users = users;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_group, container, false);
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        editTextGroupName = view.findViewById(R.id.groupNameEditText);
        Button buttonSubmit = view.findViewById(R.id.submitBtn);

        users.observe(getViewLifecycleOwner(), userDTOs -> {
            ViewGroup checkboxContainer = view.findViewById(R.id.checkboxContainer);
            checkboxContainer.removeAllViews();

            for (UserDTO user : userDTOs) {
                CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setText(user.getUsername());
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedUserIds.add(user.getId());
                    } else {
                        selectedUserIds.remove(user.getId());
                    }
                });
                checkboxContainer.addView(checkBox);
            }
        });

        buttonSubmit.setOnClickListener(v -> {
            createEvent();
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            int width = WindowManager.LayoutParams.MATCH_PARENT;
            int height = WindowManager.LayoutParams.WRAP_CONTENT;
            Objects.requireNonNull(getDialog().getWindow()).setLayout(width, height);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
    }

    private void createEvent() {
        UserGroup userGroup = new UserGroup();
        userGroup.setGroupName(editTextGroupName.getText().toString());
        userGroup.setGroupParticipants(selectedUserIds);
        contactViewModel.createMessageGroup(userGroup);
        dismiss();
    }
}
