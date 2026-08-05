package com.cpan228.catcritic.service;

import com.cpan228.catcritic.model.Role;
import com.cpan228.catcritic.model.User;
import com.cpan228.catcritic.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    public User register(String username, String rawPassword) {

        User user = new User();

        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(Role.CAT_VIEWER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void changeRole(Long userId, Role newRole) {
        userRepository.findById(userId)
                .ifPresent(user -> {
                    user.setRole(newRole);
                    userRepository.save(user);
                });
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}