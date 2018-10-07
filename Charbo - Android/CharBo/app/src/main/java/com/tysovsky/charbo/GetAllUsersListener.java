package com.tysovsky.charbo;

import com.tysovsky.charbo.Models.User;

import java.util.List;

public interface GetAllUsersListener {
    void UsersReceived(List<User> users);
}
