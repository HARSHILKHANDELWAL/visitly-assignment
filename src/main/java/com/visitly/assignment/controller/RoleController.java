package com.visitly.assignment.controller;

import com.visitly.assignment.dto.response.SuccessResponse;
import com.visitly.assignment.entity.Role;
import com.visitly.assignment.exception.BadRequestException;
import com.visitly.assignment.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    @Autowired
    private RoleRepository roleRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRole(@RequestBody Role payload) {
        String name = payload.getName();
        if (!name.startsWith("ROLE_")) name = "ROLE_" + name;
        if (roleRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Role already exists");
        }
        Role role = new Role(name);
        roleRepository.save(role);
        return ResponseEntity.ok(new SuccessResponse(true, "Role has been created successfully"));
    }
}