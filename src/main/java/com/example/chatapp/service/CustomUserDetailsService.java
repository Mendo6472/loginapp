package com.example.chatapp.service;

import com.example.chatapp.entity.User;
import com.example.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.getIsAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Create a custom UserDetails implementation that works with our password system
        return new CustomUserDetails(user, authorities);
    }

    // Inner class to handle our custom password verification
    public static class CustomUserDetails implements UserDetails {
        private final User user;
        private final List<SimpleGrantedAuthority> authorities;

        public CustomUserDetails(User user, List<SimpleGrantedAuthority> authorities) {
            this.user = user;
            this.authorities = authorities;
        }

        @Override
        public String getPassword() {
            // Return the password hash - we'll handle verification in the password encoder
            return user.getPasswordHash();
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public List<SimpleGrantedAuthority> getAuthorities() {
            return authorities;
        }

        // Getter to access the User entity
        public User getUser() {
            return user;
        }
    }
} 