package com.example.chatapp.service;

import com.example.chatapp.entity.User;
import com.example.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordService passwordService;
    
    /**
     * Inicializa el usuario administrador por defecto si no existe
     */
    @PostConstruct
    public void initializeAdminUser() {
        if (userRepository.countAdminUsers() == 0) {
            createAdminUser("admin", "admin123");
        }
    }
    
    /**
     * Crea un usuario administrador
     */
    public User createAdminUser(String username, String password) {
        if (userRepository.countAdminUsers() > 0) {
            throw new RuntimeException("Ya existe un usuario administrador");
        }
        
        PasswordService.PasswordHashResult hashResult = passwordService.hashNewPassword(password);
        User admin = new User(username, hashResult.getHash(), hashResult.getSalt(), true);
        return userRepository.save(admin);
    }
    
    /**
     * Crea un usuario común
     */
    public User createUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }
        
        PasswordService.PasswordHashResult hashResult = passwordService.hashNewPassword(password);
        User user = new User(username, hashResult.getHash(), hashResult.getSalt(), false);
        return userRepository.save(user);
    }
    
    /**
     * Autentica un usuario
     */
    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordService.verifyPassword(password, user.getPasswordHash(), user.getSalt())) {
                // Actualizar último login
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Cambia la contraseña de un usuario
     */
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!passwordService.verifyPassword(oldPassword, user.getPasswordHash(), user.getSalt())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }
        
        PasswordService.PasswordHashResult hashResult = passwordService.hashNewPassword(newPassword);
        user.setPasswordHash(hashResult.getHash());
        user.setSalt(hashResult.getSalt());
        userRepository.save(user);
    }
    
    /**
     * Resetea la contraseña de un usuario (solo admin)
     */
    public void resetUserPassword(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (user.getIsAdmin()) {
            throw new RuntimeException("No se puede resetear la contraseña del administrador");
        }
        
        // Marcar que el usuario necesita reset de contraseña
        // No cambiamos la contraseña actual, cualquier contraseña será válida
        user.setNeedsPasswordReset(true);
        userRepository.save(user);
    }
    
    /**
     * Completa el reset de contraseña del usuario
     */
    public void completePasswordReset(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!user.getNeedsPasswordReset()) {
            throw new RuntimeException("Este usuario no requiere un reset de contraseña");
        }
        
        PasswordService.PasswordHashResult hashResult = passwordService.hashNewPassword(newPassword);
        user.setPasswordHash(hashResult.getHash());
        user.setSalt(hashResult.getSalt());
        user.setNeedsPasswordReset(false);
        userRepository.save(user);
    }
    
    /**
     * Elimina un usuario (solo admin)
     */
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (user.getIsAdmin()) {
            throw new RuntimeException("No se puede eliminar el usuario administrador");
        }
        
        userRepository.delete(user);
    }
    
    /**
     * Obtiene todos los usuarios regulares (no admin)
     */
    public List<User> getAllRegularUsers() {
        return userRepository.findAllRegularUsers();
    }
    
    /**
     * Obtiene un usuario por nombre de usuario
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Verifica si un usuario es administrador
     */
    public boolean isAdmin(String username) {
        return userRepository.findByUsername(username)
                .map(User::getIsAdmin)
                .orElse(false);
    }
    
    /**
     * Obtiene la información del último login de un usuario
     */
    public LocalDateTime getLastLogin(String username) {
        return userRepository.findByUsername(username)
                .map(User::getLastLogin)
                .orElse(null);
    }
} 