package com.example.user_service.controller;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/register", produces = "text/plain")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password required");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // Returns plain text like: "Bearer eyJ..."
    @PostMapping(value = "/login", produces = "text/plain")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
        List<User> dbUsers = userRepository.findAllByUsername(req.getUsername());
        if (dbUsers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        User dbUser = dbUsers.get(0);
        String stored = dbUser.getPassword();

        boolean passwordOk;
        if (stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"))) {
            // Stored as bcrypt
            passwordOk = passwordEncoder.matches(req.getPassword(), stored);
        } else {
            // Legacy plain-text support
            passwordOk = req.getPassword().equals(stored);
        }

        if (!passwordOk) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        // Upgrade legacy plain-text password to bcrypt on successful login
        if (stored != null && !(stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"))) {
            dbUser.setPassword(passwordEncoder.encode(req.getPassword()));
            userRepository.save(dbUser);
        }

        String token = jwtUtil.generateToken(dbUser.getUsername());
        return ResponseEntity.ok("Bearer " + token);
    }
}

