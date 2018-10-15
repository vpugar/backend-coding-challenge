package com.engagetech.expenses.service;

import com.engagetech.expenses.model.User;
import com.engagetech.expenses.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DdUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User getUser(long userId) throws UserNotFoundException {
        return userRepository.findById(userId)
              .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
