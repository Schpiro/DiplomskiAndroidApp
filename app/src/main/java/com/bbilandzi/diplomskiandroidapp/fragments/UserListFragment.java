package com.bbilandzi.diplomskiandroidapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bbilandzi.diplomskiandroidapp.R;
import com.bbilandzi.diplomskiandroidapp.adapter.UserAdapter;
import com.bbilandzi.diplomskiandroidapp.viewmodel.ContactViewModel;

public class UserListFragment extends Fragment {

    private UserAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_list, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter();
        recyclerView.setAdapter(adapter);

        ContactViewModel viewModel = new ViewModelProvider(requireActivity()).get(ContactViewModel.class);
        viewModel.getFetchedUsers().observe(getViewLifecycleOwner(), users -> adapter.setUsers(users));

        viewModel.getAllUsers();

        return rootView;
    }
}
