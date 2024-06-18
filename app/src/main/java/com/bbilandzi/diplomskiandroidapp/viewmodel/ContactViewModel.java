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
    private ContactsRepository contactsRepository;
    private MutableLiveData<List<UserDTO>> fetchedUsers = new MutableLiveData<>();
    private MutableLiveData<List<UserGroup>> fetchedGroups = new MutableLiveData<>();

    @Inject
    public ContactViewModel(ContactsRepository contactsRepository) {
        this.contactsRepository = contactsRepository;
    }

    public LiveData<List<UserDTO>> getFetchedUsers() {
        return fetchedUsers;
    }

    public LiveData<List<UserGroup>> getFetchedGroups() {
        return fetchedGroups;
    }

    public void getAllUsers() {
        contactsRepository.getAllUsers().enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.isSuccessful()) {
                    List<UserDTO> users = response.body();
                    fetchedUsers.setValue(users);
                    Log.d("ContactViewModel", users.toString());
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable throwable) {
                Log.e("ContactViewModel Error", "Failed: " + throwable.getMessage());
            }
        });
    }

    public void getAllUserGroups() {
        contactsRepository.getAllUserGroups().enqueue(new Callback<List<UserGroup>>() {
            @Override
            public void onResponse(Call<List<UserGroup>> call, Response<List<UserGroup>> response) {
                if (response.isSuccessful()) {
                    List<UserGroup> groups = response.body();
                    fetchedGroups.setValue(groups);
                    Log.d("ContactViewModel", groups.toString());
                }
            }

            @Override
            public void onFailure(Call<List<UserGroup>> call, Throwable throwable) {
                Log.e("ContactViewModel Error", "Failed: " + throwable.getMessage());
            }
        });
    }
}