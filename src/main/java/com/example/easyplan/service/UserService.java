package com.example.easyplan.service;

import com.example.easyplan.domain.entity.User.User;
import com.example.easyplan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByUserKey(String userKey) {
        return userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(()->new IllegalArgumentException("Unexpected user"));
    }

    public User findByName(String userName) {
        return userRepository.findByName(userName)
                .orElseThrow(()->new IllegalArgumentException("Unexpected user"));
    }
}
