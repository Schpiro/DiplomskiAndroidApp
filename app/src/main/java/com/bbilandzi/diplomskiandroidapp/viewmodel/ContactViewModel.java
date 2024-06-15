package com.bbilandzi.diplomskiandroidapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbilandzi.diplomskiandroidapp.model.UserDTO;
import com.bbilandzi.diplomskiandroidapp.model.UserGroup;
import com.bbilandzi.diplomskiandroidapp.repository.ContactsRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ContactViewModel extends ViewModel {
    private ContactsRepository contactsRepository;
    private MutableLiveData<List<UserDTO>> fetchedUsers = new MutableLiveData<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();


    @Inject
    public ContactViewModel(ContactsRepository contactsRepository) {
        this.contactsRepository = contactsRepository;
        getAllUsers();
    }

    public LiveData<List<UserDTO>> getFetchedUsers() {
        return fetchedUsers;
    }

    public void getAllUsers() {
        Call<List<UserDTO>> call = contactsRepository.getAllUsers();

        call.enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.isSuccessful()) {
                    List<UserDTO> users = response.body();
                    fetchedUsers.postValue(users); // Update LiveData with fetched data
                    Log.d("Users", users.toString());
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable throwable) {
                Log.e("GetAllUsers Error", "Failed: " + throwable.getMessage());
            }
        });
    }

    public Call<List<UserGroup>> getAllUserGroups() {
        return contactsRepository.getAllUserGroups();
    }

    public Call<List<UserDTO>> getAllUsersInGroup(Long id) {
        return contactsRepository.getAllUsersInGroup(id);
    }
}
