package com.bbilandzi.diplomskiandroidapp.fragments;

import static com.bbilandzi.diplomskiandroidapp.utils.DateTimeUtil.getDateInMillis;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.model.EventDTO;
import com.bbilandzi.diplomskiandroidapp.model.UserDTO;
import com.bbilandzi.diplomskiandroidapp.model.UserGroup;
import com.bbilandzi.diplomskiandroidapp.utils.AuthUtils;
import com.bbilandzi.diplomskiandroidapp.viewmodel.AuthViewModel;
import com.bbilandzi.diplomskiandroidapp.viewmodel.ContactViewModel;

import java.util.ArrayList;
import java.util.List;

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

    private void createEvent() {
        UserGroup userGroup = new UserGroup();
        userGroup.setGroupName(editTextGroupName.getText().toString());
        userGroup.setGroupParticipants(selectedUserIds);
        contactViewModel.createMessageGroup(userGroup);
        dismiss();
    }
}
