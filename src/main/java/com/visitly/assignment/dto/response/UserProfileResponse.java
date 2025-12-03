package com.visitly.assignment.dto.response;

import java.util.List;

public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    public UserProfileResponse(Long id, String username, String email, List<String> roles) {
        this.id = id; this.username = username; this.email = email; this.roles = roles;
    }
    // getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public List<String> getRoles() { return roles; }
}