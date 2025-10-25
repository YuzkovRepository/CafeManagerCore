package com.example.CafeManagerCore.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;

    @Column(nullable = false, length = 20, unique = true)
    String login;
    @Column(nullable = false, length = 255)
    String password_hash;
    @Column(nullable = false, length = 100, unique = true)
    String email;
    @Column(nullable = false, length = 15)
    String phone;

    @Column(nullable = false, length = 60)
    String firstname;
    @Column(nullable = false, length = 80)
    String lastname;
    @Column(length = 80)
    String surname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Status status = Status.ACTIVE;

    @Column(nullable = false)
    LocalDateTime createdAt;
    @Column(nullable = false)
    LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Status {
        ACTIVE, INACTIVE, BLOCKED
    }


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new HashSet<>();
}