package com.bbilandzi.diplomskiandroidapp.viewmodel;

import static com.bbilandzi.diplomskiandroidapp.utils.MessageTypes.NEW_GROUP;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bbilandzi.diplomskiandroidapp.model.UserDTO;
import com.bbilandzi.diplomskiandroidapp.model.UserGroup;
import com.bbilandzi.diplomskiandroidapp.model.WebsocketMessageDTO;
import com.bbilandzi.diplomskiandroidapp.repository.ContactsRepository;
import com.bbilandzi.diplomskiandroidapp.utils.WebSocketManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ContactViewModel extends ViewModel {
    private final WebSocketManager webSocketManager;
    private ContactsRepository contactsRepository;
    private MutableLiveData<List<UserDTO>> fetchedUsers = new MutableLiveData<>();
    private MutableLiveData<List<UserGroup>> fetchedGroups = new MutableLiveData<>();
    private final Gson gson = new Gson();


    @Inject
    public ContactViewModel(ContactsRepository contactsRepository, WebSocketManager webSocketManager) {
        this.webSocketManager = webSocketManager;
        this.contactsRepository = contactsRepository;
        webSocketManager.getMessageLiveData(NEW_GROUP).observeForever(this::onNewUserGroupReceived);
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
            public void onResponse(@NonNull Call<List<UserDTO>> call, @NonNull Response<List<UserDTO>> response) {
                if (response.isSuccessful()) {
                    List<UserDTO> users = response.body();
                    fetchedUsers.setValue(users);
                    assert users != null;
                    Log.d("ContactViewModel", users.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserDTO>> call, @NonNull Throwable throwable) {
                Log.e("ContactViewModel Error", "Failed: " + throwable.getMessage());
            }
        });
    }

    public void getAllUserGroups() {
        contactsRepository.getAllUserGroups().enqueue(new Callback<List<UserGroup>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserGroup>> call, @NonNull Response<List<UserGroup>> response) {
                if (response.isSuccessful()) {
                    List<UserGroup> groups = response.body();
                    fetchedGroups.setValue(groups);
                    assert groups != null;
                    Log.d("ContactViewModel", groups.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserGroup>> call, @NonNull Throwable throwable) {
                Log.e("ContactViewModel Error", "Failed: " + throwable.getMessage());
            }
        });
    }

    public void createMessageGroup(UserGroup userGroup) {
        contactsRepository.createMessageGroup(userGroup).enqueue(new Callback<UserGroup>() {
            @Override
            public void onResponse(@NonNull Call<UserGroup> call, @NonNull Response<UserGroup> response) {
                if (response.isSuccessful()) {
                    UserGroup group = response.body();
                    WebsocketMessageDTO message = new WebsocketMessageDTO();
                    message.setType(NEW_GROUP);
                    message.setPayload(gson.toJson(group));
                    webSocketManager.sendMessage(message);
                    assert group != null;
                    Log.d("ContactViewModel", group.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserGroup> call, @NonNull Throwable throwable) {
                Log.e("ContactViewModel Error", "Failed: " + throwable.getMessage());
            }
        });
    }

    private void onNewUserGroupReceived(WebsocketMessageDTO websocketMessageDTO) {
        UserGroup newUserGroup = gson.fromJson(gson.toJson(websocketMessageDTO.getPayload()), UserGroup.class);
        List<UserGroup> currentList = fetchedGroups.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(newUserGroup);
        fetchedGroups.postValue(currentList);
    }

}