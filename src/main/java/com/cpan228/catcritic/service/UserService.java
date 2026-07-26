package com.cpan228.catcritic.service;

import com.cpan228.catcritic.model.User;
import com.cpan228.catcritic.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    public User register(String username, String rawPassword) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hash(rawPassword));
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public Optional<User> authenticate(String username, String rawPassword) {
        return userRepository.findByUsernameIgnoreCase(username)
                .filter(user -> user.getPasswordHash().equals(hash(rawPassword)));
    }

    private String hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
