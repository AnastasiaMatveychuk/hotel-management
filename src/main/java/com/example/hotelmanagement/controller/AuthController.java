package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.entity.User;
import com.example.hotelmanagement.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Добавлен импорт
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    // Регистрация нового пользователя
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }
        user.setRole(User.Role.USER); // Назначаем роль USER по умолчанию
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // Аутентификация пользователя
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody User loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok("User authenticated successfully");
    }

    // Получение информации о текущем пользователе
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Создаём DTO для ответа
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getRole().name());

        return ResponseEntity.ok(userDTO);
    }

    // Получение списка всех пользователей (только для администраторов)
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getRole().name()))
                .toList();
        return ResponseEntity.ok(userDTOs);
    }

    // Обновление роли пользователя (только для администраторов)
    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody RoleUpdateRequest roleUpdateRequest) {
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        User user = optionalUser.get();
        try {
            user.setRole(User.Role.valueOf(roleUpdateRequest.getRole()));
            userService.saveUser(user);
            return ResponseEntity.ok("User role updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role");
        }
    }

    // Внутренний класс DTO
    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserDTO {
        private Long id;
        private String username;
        private String role;
    }

    // Внутренний класс запроса для обновления роли
    @Getter
    @Setter
    public static class RoleUpdateRequest {
        private String role;
    }
}
