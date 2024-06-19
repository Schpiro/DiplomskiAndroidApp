package com.bbilandzi.diplomskiandroidapp.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.model.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupDialogFragment extends DialogFragment {

    private final LiveData<List<UserDTO>> users;
    private EditText editTextGroupName;
    private CreateGroupDialogListener listener;
    private List<Long> selectedUserIds = new ArrayList<>();


    public interface CreateGroupDialogListener {
        void onSubmit(String groupName, List<Long> selectedUser);
    }

    public CreateGroupDialogFragment(LiveData<List<UserDTO>> users, CreateGroupDialogListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_group, container, false);

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
            String groupName = editTextGroupName.getText().toString();
            listener.onSubmit(groupName, selectedUserIds);
            dismiss();
        });

        return view;
    }
}
