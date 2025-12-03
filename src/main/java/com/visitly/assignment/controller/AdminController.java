package com.visitly.assignment.controller;

import com.visitly.assignment.dto.response.SuccessResponse;
import com.visitly.assignment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> stats() {
        var users = userRepository.findAll();
        var lastLogins = users.stream()
                .collect(Collectors.toMap(u -> u.getEmail(), u -> Optional.ofNullable(u.getLastLogin())));

        return ResponseEntity.ok(new SuccessResponse(true,  Map.of(
                "totalUsers", users.size(),
                "lastLogins", lastLogins
        )));

    }
}