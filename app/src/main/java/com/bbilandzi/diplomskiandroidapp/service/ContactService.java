package com.bbilandzi.diplomskiandroidapp.service;

import com.bbilandzi.diplomskiandroidapp.model.UserDTO;
import com.bbilandzi.diplomskiandroidapp.model.UserGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ContactService {
    @GET("users")
    Call<List<UserDTO>> getAllUsers();

    @GET("users/groups")
    Call<List<UserGroup>> getAllUserGroups();

    @GET("users/groups/{id}")
    Call<List<UserDTO>> getUsersInGroup(@Path("id") Long id);
}
