package com.engagetech.expenses.service;

import com.engagetech.expenses.model.User;

public interface UserService {

    User getUser(long userId) throws UserNotFoundException;

}
