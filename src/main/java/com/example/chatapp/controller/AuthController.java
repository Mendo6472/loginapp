package com.example.chatapp.controller;

import com.example.chatapp.dto.LoginRequest;
import com.example.chatapp.dto.PasswordResetRequest;
import com.example.chatapp.dto.RegisterRequest;
import com.example.chatapp.entity.User;
import com.example.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Optional;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String home() {
        return "redirect:login";
    }
    
    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("message", "¡Bienvenido a LoginApp! La aplicación está funcionando correctamente.");
        return "welcome";
    }
    
    @GetMapping("/login")
    public String loginPage(Model model, 
                           @RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout) {
        
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión exitosamente");
        }
        
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginRequest loginRequest,
                       BindingResult bindingResult,
                       HttpSession session,
                       HttpServletRequest request,
                       RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "login";
        }
        
        Optional<User> userOpt = userService.authenticateUser(
            loginRequest.getUsername(), 
            loginRequest.getPassword()
        );
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Verificar si el usuario necesita resetear su contraseña
            if (user.getNeedsPasswordReset()) {
                session.setAttribute("passwordResetUser", user.getUsername());
                redirectAttributes.addFlashAttribute("message", 
                    "Tu contraseña ha sido reseteada. Debes establecer una nueva contraseña.");
                return "redirect:password-reset";
            }
            
            session.setAttribute("currentUser", user);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("isAdmin", user.getIsAdmin());
            
            if (user.getIsAdmin()) {
                return "redirect:admin/dashboard";
            } else {
                return "redirect:user/dashboard";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Credenciales inválidas");
            return "redirect:login?error=true";
        }
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        if (!registerRequest.isPasswordConfirmed()) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", 
                "Las contraseñas no coinciden");
            return "register";
        }
        
        try {
            userService.createUser(registerRequest.getUsername(), registerRequest.getPassword());
            redirectAttributes.addFlashAttribute("message", 
                "Usuario registrado exitosamente. Puedes iniciar sesión ahora.");
            return "redirect:login";
        } catch (RuntimeException e) {
            bindingResult.rejectValue("username", "error.username", e.getMessage());
            return "register";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:login?logout=true";
    }
    
    @GetMapping("/password-reset")
    public String passwordResetPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String username = (String) session.getAttribute("passwordResetUser");
        
        if (username == null) {
            redirectAttributes.addFlashAttribute("error", "Acceso no autorizado");
            return "redirect:login";
        }
        
        model.addAttribute("passwordResetRequest", new PasswordResetRequest());
        model.addAttribute("username", username);
        return "password-reset";
    }
    
    @PostMapping("/password-reset")
    public String processPasswordReset(@Valid @ModelAttribute PasswordResetRequest passwordResetRequest,
                                     BindingResult bindingResult,
                                     HttpSession session,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        
        String username = (String) session.getAttribute("passwordResetUser");
        
        if (username == null) {
            redirectAttributes.addFlashAttribute("error", "Acceso no autorizado");
            return "redirect:login";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("username", username);
            return "password-reset";
        }
        
        if (!passwordResetRequest.isPasswordConfirmed()) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", 
                "Las contraseñas no coinciden");
            model.addAttribute("username", username);
            return "password-reset";
        }
        
        try {
            userService.completePasswordReset(username, passwordResetRequest.getNewPassword());
            session.removeAttribute("passwordResetUser");
            redirectAttributes.addFlashAttribute("message", 
                "Tu contraseña ha sido actualizada exitosamente. Puedes iniciar sesión con tu nueva contraseña.");
            return "redirect:login";
        } catch (RuntimeException e) {
            bindingResult.rejectValue("newPassword", "error.newPassword", e.getMessage());
            model.addAttribute("username", username);
            return "password-reset";
        }
    }
} 