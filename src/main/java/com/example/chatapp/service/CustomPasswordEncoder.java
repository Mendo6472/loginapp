package com.example.chatapp.service;

import com.example.chatapp.entity.User;
import com.example.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomPasswordEncoder implements PasswordEncoder {

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String encode(CharSequence rawPassword) {
        // This method is used for encoding new passwords
        // We return the raw password since our system handles hashing differently
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // Find the user by their password hash to get the salt
        // This is more reliable than using ThreadLocal
        User user = userRepository.findAll().stream()
                .filter(u -> u.getPasswordHash().equals(encodedPassword))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return false;
        }

        // Si el usuario necesita reset de contraseña, aceptar cualquier contraseña
        if (user.getNeedsPasswordReset()) {
            return true;
        }

        // Verificación normal de contraseña
        return passwordService.verifyPassword(rawPassword.toString(), user.getPasswordHash(), user.getSalt());
    }
} 