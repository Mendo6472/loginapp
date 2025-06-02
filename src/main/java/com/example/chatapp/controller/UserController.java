package com.example.chatapp.controller;

import com.example.chatapp.dto.ChangePasswordRequest;
import com.example.chatapp.entity.User;
import com.example.chatapp.service.CustomUserDetailsService;
import com.example.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/dashboard")
    public String userDashboard(Model model, HttpServletRequest request) {
        // Get authenticated user from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:../login";
        }
        
        CustomUserDetailsService.CustomUserDetails userDetails = 
            (CustomUserDetailsService.CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        
        // Si es admin, redirigir al dashboard de admin
        if (user.getIsAdmin()) {
            return "redirect:../admin/dashboard";
        }
        
        model.addAttribute("currentUser", user);
        
        // Get previous last login from session
        HttpSession session = request.getSession();
        LocalDateTime previousLastLogin = (LocalDateTime) session.getAttribute("previousLastLogin");
        
        if (previousLastLogin != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            model.addAttribute("lastLogin", previousLastLogin.format(formatter));
        } else {
            model.addAttribute("lastLogin", "Primer inicio de sesión");
        }
        
        return "user/dashboard";
    }
    
    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        // Get authenticated user from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:../login";
        }
        
        CustomUserDetailsService.CustomUserDetails userDetails = 
            (CustomUserDetailsService.CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        model.addAttribute("currentUser", username);
        
        return "user/change-password";
    }
    
    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute ChangePasswordRequest changePasswordRequest,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        
        // Get authenticated user from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:../login";
        }
        
        CustomUserDetailsService.CustomUserDetails userDetails = 
            (CustomUserDetailsService.CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        if (bindingResult.hasErrors()) {
            return "user/change-password";
        }
        
        if (!changePasswordRequest.isPasswordConfirmed()) {
            bindingResult.rejectValue("confirmNewPassword", "error.confirmNewPassword", 
                "Las contraseñas no coinciden");
            return "user/change-password";
        }
        
        try {
            userService.changePassword(username, 
                changePasswordRequest.getCurrentPassword(), 
                changePasswordRequest.getNewPassword());
            
            redirectAttributes.addFlashAttribute("message", 
                "Contraseña cambiada exitosamente");
            return "redirect:dashboard";
        } catch (RuntimeException e) {
            bindingResult.rejectValue("currentPassword", "error.currentPassword", e.getMessage());
            return "user/change-password";
        }
    }
    
    @GetMapping("/profile")
    public String userProfile(Model model, HttpServletRequest request) {
        // Get authenticated user from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:../login";
        }
        
        CustomUserDetailsService.CustomUserDetails userDetails = 
            (CustomUserDetailsService.CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        
        model.addAttribute("user", user);
        
        // Get previous last login from session
        HttpSession session = request.getSession();
        LocalDateTime previousLastLogin = (LocalDateTime) session.getAttribute("previousLastLogin");
        
        if (previousLastLogin != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            model.addAttribute("lastLogin", previousLastLogin.format(formatter));
        } else {
            model.addAttribute("lastLogin", "Primer inicio de sesión");
        }
        
        DateTimeFormatter createdFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        model.addAttribute("createdAt", user.getCreatedAt().format(createdFormatter));
        
        return "user/profile";
    }
} 