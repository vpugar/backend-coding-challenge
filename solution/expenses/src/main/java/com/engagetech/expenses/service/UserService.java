package com.engagetech.expenses.service;

import com.engagetech.expenses.model.User;

import java.util.Optional;

/**
 * For getting users from storage.
 */
public interface UserService {

    User getUser(long userId) throws UserNotFoundException;

    Optional<User> getUser(String username);

}
