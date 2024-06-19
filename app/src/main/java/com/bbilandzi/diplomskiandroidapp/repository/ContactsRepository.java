package com.bbilandzi.diplomskiandroidapp.repository;

import com.bbilandzi.diplomskiandroidapp.model.UserDTO;
import com.bbilandzi.diplomskiandroidapp.model.UserGroup;
import com.bbilandzi.diplomskiandroidapp.service.ContactService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;

public class ContactsRepository {
    ContactService contactService;

    @Inject
    public ContactsRepository(Retrofit client) {
        this.contactService = client.create(ContactService.class);
    }

    public Call<List<UserDTO>> getAllUsers() {
        return contactService.getAllUsers();
    }

    public Call<List<UserGroup>> getAllUserGroups() {
        return contactService.getAllUserGroups();
    }

    public Call<List<UserDTO>> getAllUsersInGroup(Long id) {
        return contactService.getUsersInGroup(id);
    }

    public Call<UserGroup> createMessageGroup(@Body UserGroup userGroup) {
        return contactService.createMessageGroup(userGroup);
    }

}
