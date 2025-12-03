package com.visitly.assignment.dto.request;

import java.util.List;

public class AssignRolesRequest {
    private List<String> roles;
    // getters / setters
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}