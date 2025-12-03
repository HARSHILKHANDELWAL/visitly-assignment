package com.visitly.assignment.service;

import com.visitly.assignment.dto.request.LoginRequest;
import com.visitly.assignment.dto.request.RegisterRequest;
import com.visitly.assignment.dto.response.JwtResponse;
import com.visitly.assignment.dto.response.UserProfileResponse;
import com.visitly.assignment.entity.Role;
import com.visitly.assignment.entity.User;
import com.visitly.assignment.events.EventPublisher;
import com.visitly.assignment.exception.BadRequestException;
import com.visitly.assignment.exception.ResourceNotFoundException;
import com.visitly.assignment.repository.RoleRepository;
import com.visitly.assignment.repository.UserRepository;
import com.visitly.assignment.security.JwtUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final Environment environment;
    private final EventPublisher eventPublisher;
    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(Environment environment,
                       EventPublisher eventPublisher,
                       AuthenticationManager authManager,
                       UserRepository userRepo,
                       RoleRepository roleRepo,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils) {

        this.environment = environment;
        this.eventPublisher = eventPublisher;
        this.authManager = authManager;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public String register(RegisterRequest req) {

        if (userRepo.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        Role userRole = roleRepo.findByName("ROLE_USER")
                .orElseGet(() -> roleRepo.save(new Role("ROLE_USER")));

        user.getRoles().add(userRole);
        userRepo.save(user);

        if (Arrays.asList(environment.getActiveProfiles()).contains("docker")) {
            eventPublisher.publishRegistration(user.getId(), user.getEmail());
        }

        return "User registered successfully";
    }

    public JwtResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        if (auth == null || !auth.isAuthenticated()) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String jwt = jwtUtils.generateJwtToken(auth);

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setLastLogin(Instant.now());
        userRepo.save(user);

        if (Arrays.asList(environment.getActiveProfiles()).contains("docker")) {
            eventPublisher.publishLogin(user.getId(), user.getEmail());
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return new JwtResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), roles);
    }

    public UserProfileResponse me(Authentication auth) {
        String email = auth.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(), roles);
    }
}
