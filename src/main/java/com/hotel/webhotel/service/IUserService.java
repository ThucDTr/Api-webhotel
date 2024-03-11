package com.hotel.webhotel.service;

import java.util.List;

import com.hotel.webhotel.model.User;

public interface IUserService {
    
    User registerUser(User user);

    List<User> getUsers();

    void removeUser(String email);

    User getUser(String email);

}
