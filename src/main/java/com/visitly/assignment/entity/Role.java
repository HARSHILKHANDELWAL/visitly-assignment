package com.visitly.assignment.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // store names like "ROLE_USER", "ROLE_ADMIN"
    @Column(nullable = false, unique = true)
    private String name;

    public Role() {}
    public Role(String name) { this.name = name; }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}