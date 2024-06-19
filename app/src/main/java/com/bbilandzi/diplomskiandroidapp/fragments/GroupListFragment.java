package com.bbilandzi.diplomskiandroidapp.fragments;

import android.content.Intent;
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
import com.bbilandzi.diplomskiandroidapp.activity.MessengerActivity;
import com.bbilandzi.diplomskiandroidapp.adapter.GroupAdapter;
import com.bbilandzi.diplomskiandroidapp.model.UserGroup;
import com.bbilandzi.diplomskiandroidapp.viewmodel.ContactViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GroupListFragment extends Fragment {

    private ContactViewModel contactsViewModel;
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupAdapter = new GroupAdapter();
        recyclerView.setAdapter(groupAdapter);

        contactsViewModel = new ViewModelProvider(requireActivity()).get(ContactViewModel.class);
        contactsViewModel.getFetchedGroups().observe(getViewLifecycleOwner(), groups -> {
            groupAdapter.setGroups(groups);
        });

        contactsViewModel.getAllUserGroups();

        return view;
    }

}
