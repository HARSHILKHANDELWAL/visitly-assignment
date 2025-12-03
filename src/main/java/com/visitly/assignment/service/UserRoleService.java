package com.visitly.assignment.service;

import com.visitly.assignment.dto.request.AssignRolesRequest;
import com.visitly.assignment.entity.Role;
import com.visitly.assignment.entity.User;
import com.visitly.assignment.exception.ResourceNotFoundException;
import com.visitly.assignment.repository.RoleRepository;
import com.visitly.assignment.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    public UserRoleService(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @Transactional
    public String assignRoles(Long userId, AssignRolesRequest req) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.getRoles().clear();

        for (String rname : req.getRoles()) {
            String normalized = rname.startsWith("ROLE_") ? rname : "ROLE_" + rname;

            Role role = roleRepo.findByName(normalized)
                    .orElseGet(() -> roleRepo.save(new Role(normalized)));

            user.getRoles().add(role);
        }

        userRepo.save(user);
        return "Roles updated";
    }
}
