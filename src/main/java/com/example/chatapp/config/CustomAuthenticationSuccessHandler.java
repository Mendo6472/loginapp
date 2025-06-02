package com.example.chatapp.config;

import com.example.chatapp.entity.User;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        // Obtener el usuario autenticado
        CustomUserDetailsService.CustomUserDetails userDetails = 
            (CustomUserDetailsService.CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        
        HttpSession session = request.getSession();
        
        // Obtener el context path para construir URLs correctas
        String contextPath = request.getContextPath();
        
        // Verificar si el usuario necesita resetear su contraseña
        if (user.getNeedsPasswordReset()) {
            // Guardar el usuario en sesión para el reset
            session.setAttribute("passwordResetUser", user.getUsername());
            session.setAttribute("message", "Tu contraseña ha sido reseteada. Debes establecer una nueva contraseña.");
            
            // Redirigir a la página de reset
            response.sendRedirect(contextPath + "/password-reset");
            return;
        }
        
        // Guardar el último login anterior en la sesión antes de actualizarlo
        if (user.getLastLogin() != null) {
            session.setAttribute("previousLastLogin", user.getLastLogin());
        }
        
        // Actualizar último login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        // Guardar información del usuario en sesión
        session.setAttribute("currentUser", user);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("isAdmin", user.getIsAdmin());
        
        // Redirigir según el rol
        if (user.getIsAdmin()) {
            response.sendRedirect(contextPath + "/admin/dashboard");
        } else {
            response.sendRedirect(contextPath + "/user/dashboard");
        }
    }
} 