package com.bbilandzi.diplomskiandroidapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbilandzi.diplomskiandroidapp.model.UserDTO;
import com.bbilandzi.diplomskiandroidapp.model.UserGroup;
import com.bbilandzi.diplomskiandroidapp.repository.ContactsRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ContactViewModel extends ViewModel {
    private final ContactsRepository contactsRepository;
    private final MutableLiveData<List<UserDTO>> fetchedUsers = new MutableLiveData<>();
    private final MutableLiveData<List<UserGroup>> fetchedGroups = new MutableLiveData<>();

    @Inject
    public ContactViewModel(ContactsRepository contactsRepository) {
        this.contactsRepository = contactsRepository;
        getAllUsers();
        getAllUserGroups();
    }

    public LiveData<List<UserDTO>> getFetchedUsers() {
        return fetchedUsers;
    }

    public LiveData<List<UserGroup>> getFetchedGroups() {
        return fetchedGroups;
    }

    public void getAllUsers() {
        Call<List<UserDTO>> call = contactsRepository.getAllUsers();

        call.enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.isSuccessful()) {
                    List<UserDTO> users = response.body();
                    fetchedUsers.setValue(users);
                    Log.d("Users", users.toString());
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable throwable) {
                Log.e("GetAllUsers Error", "Failed: " + throwable.getMessage());
            }
        });
    }

    public void getAllUserGroups() {
        Call<List<UserGroup>> call = contactsRepository.getAllUserGroups();

        call.enqueue(new Callback<List<UserGroup>>() {
            @Override
            public void onResponse(Call<List<UserGroup>> call, Response<List<UserGroup>> response) {
                if (response.isSuccessful()) {
                    List<UserGroup> groups = response.body();
                    fetchedGroups.setValue(groups);
                    Log.d("Groups", groups.toString());
                }
            }

            @Override
            public void onFailure(Call<List<UserGroup>> call, Throwable throwable) {
                Log.e("GetAllGroups Error", "Failed: " + throwable.getMessage());
            }
        });
    }
}