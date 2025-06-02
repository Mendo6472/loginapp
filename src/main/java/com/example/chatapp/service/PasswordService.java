package com.example.chatapp.service;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Service
public class PasswordService {
    
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 100000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 32;
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Genera un salt aleatorio
     */
    public String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hashea una contraseña usando PBKDF2 con el salt proporcionado
     */
    public String hashPassword(String password, String salt) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }
    
    /**
     * Verifica si una contraseña coincide con el hash almacenado
     */
    public boolean verifyPassword(String password, String storedHash, String salt) {
        String hashedPassword = hashPassword(password, salt);
        return hashedPassword.equals(storedHash);
    }
    
    /**
     * Genera un hash completo (salt + hash) para una nueva contraseña
     */
    public PasswordHashResult hashNewPassword(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return new PasswordHashResult(hash, salt);
    }
    
    /**
     * Clase para retornar el resultado del hash de contraseña
     */
    public static class PasswordHashResult {
        private final String hash;
        private final String salt;
        
        public PasswordHashResult(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }
        
        public String getHash() {
            return hash;
        }
        
        public String getSalt() {
            return salt;
        }
    }
} 