package com.visitly.assignment.controller;

import com.visitly.assignment.dto.request.AssignRolesRequest;
import com.visitly.assignment.dto.request.LoginRequest;
import com.visitly.assignment.dto.request.RegisterRequest;
import com.visitly.assignment.dto.response.SuccessResponse;
import com.visitly.assignment.service.AuthService;
import com.visitly.assignment.service.UserRoleService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(
        value = "/api/users"
)
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
     UserRoleService userRoleService;

    public AuthController(AuthService authService, UserRoleService userRoleService) {
        this.authService = authService;
        this.userRoleService = userRoleService;
    }
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        String result = authService.register(req);
        return ResponseEntity.ok(new SuccessResponse(true,result));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(new SuccessResponse(true, authService.login(req)));

    }

    @GetMapping("/me")
    public ResponseEntity<?> me(org.springframework.security.core.Authentication authentication) {
        return ResponseEntity.ok(new SuccessResponse(true, authService.me(authentication)));
    }

    @PostMapping("/{userId}/roles")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> assignRoles(@PathVariable Long userId, @RequestBody AssignRolesRequest req) {
        return ResponseEntity.ok(new SuccessResponse(true, userRoleService.assignRoles(userId,req)));

    }
}