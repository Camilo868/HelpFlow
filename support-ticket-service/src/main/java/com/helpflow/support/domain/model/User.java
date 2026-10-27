package com.helpflow.support.domain.model;

import com.helpflow.support.domain.enums.Role;
import com.helpflow.support.domain.enums.UserStatus;
import com.helpflow.support.domain.exception.InactiveUserException;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private final String document;
    private String name;
    private String corporateEmail;
    private String password;
    private String headquarters;
    private String department;
    private Role role;
    private UserStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(Long id, String document, String name, String corporateEmail, String password,
                String headquarters, String department, Role role, UserStatus status,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.document = document;
        this.name = name;
        this.corporateEmail = corporateEmail;
        this.password = password;
        this.headquarters = headquarters;
        this.department = department;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User createNew(String document, String name, String corporateEmail,
                                 String password, String headquarters, String department, Role role) {
        LocalDateTime now = LocalDateTime.now();
        return new User(null, document, name, corporateEmail, password,
                headquarters, department, role, UserStatus.ACTIVO, now, now);
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVO;
    }

    public void validateCanCreateTickets() {
        if (!isActive()) {
            throw new InactiveUserException(String.format("User '%s' is INACTIVE and cannot create tickets", corporateEmail));
        }
    }

    public void updateProfile(String name, String headquarters, String department) {
        this.name = name;
        this.headquarters = headquarters;
        this.department = department;
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(String newHashedPassword) {
        this.password = newHashedPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = UserStatus.ACTIVO;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVO;
        this.updatedAt = LocalDateTime.now();
    }

    public void assignId(Long id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    // Getters only
    public Long getId() {
        return id;
    }

    public String getDocument() {
        return document;
    }

    public String getName() {
        return name;
    }

    public String getCorporateEmail() {
        return corporateEmail;
    }

    public String getPassword() {
        return password;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public String getDepartment() {
        return department;
    }

    public Role getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
