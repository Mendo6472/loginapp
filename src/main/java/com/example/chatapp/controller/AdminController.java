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

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    private boolean isAdminAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return false;
        }
        
        CustomUserDetailsService.CustomUserDetails userDetails = 
            (CustomUserDetailsService.CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser().getIsAdmin();
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetailsService.CustomUserDetails userDetails = 
            (CustomUserDetailsService.CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
    
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        if (!isAdminAuthenticated()) {
            return "redirect:../login";
        }
        
        List<User> users = userService.getAllRegularUsers();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", getCurrentUsername());
        
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String listUsers(Model model) {
        if (!isAdminAuthenticated()) {
            return "redirect:../login";
        }
        
        List<User> users = userService.getAllRegularUsers();
        model.addAttribute("users", users);
        model.addAttribute("currentUser", getCurrentUsername());
        
        return "admin/users";
    }
    
    @PostMapping("/users/{username}/delete")
    public String deleteUser(@PathVariable String username,
                            RedirectAttributes redirectAttributes) {
        
        if (!isAdminAuthenticated()) {
            return "redirect:../../login";
        }
        
        try {
            userService.deleteUser(username);
            redirectAttributes.addFlashAttribute("message", 
                "Usuario '" + username + "' eliminado exitosamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }
    
    @PostMapping("/users/{username}/reset-password")
    public String resetUserPassword(@PathVariable String username,
                                   RedirectAttributes redirectAttributes) {
        
        if (!isAdminAuthenticated()) {
            return "redirect:../../login";
        }
        
        try {
            userService.resetUserPassword(username);
            redirectAttributes.addFlashAttribute("message", 
                "Contraseña del usuario '" + username + "' reseteada exitosamente. " +
                "El usuario puede iniciar sesión con cualquier contraseña y " +
                "será redirigido automáticamente para establecer una nueva contraseña.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        if (!isAdminAuthenticated()) {
            return "redirect:../login";
        }
        
        String username = getCurrentUsername();
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        model.addAttribute("currentUser", username);
        
        return "admin/change-password";
    }
    
    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute ChangePasswordRequest changePasswordRequest,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        
        if (!isAdminAuthenticated()) {
            return "redirect:../login";
        }
        
        String username = getCurrentUsername();
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", username);
            return "admin/change-password";
        }
        
        if (!changePasswordRequest.isPasswordConfirmed()) {
            bindingResult.rejectValue("confirmNewPassword", "error.confirmNewPassword", 
                "Las contraseñas no coinciden");
            model.addAttribute("currentUser", username);
            return "admin/change-password";
        }
        
        try {
            userService.changePassword(username, 
                changePasswordRequest.getCurrentPassword(), 
                changePasswordRequest.getNewPassword());
            
            redirectAttributes.addFlashAttribute("message", 
                "Contraseña cambiada exitosamente");
            return "redirect:/admin/dashboard";
        } catch (RuntimeException e) {
            bindingResult.rejectValue("currentPassword", "error.currentPassword", e.getMessage());
            model.addAttribute("currentUser", username);
            return "admin/change-password";
        }
    }
}