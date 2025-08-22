package com.example.CafeManagerCore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "guests")
public class Guest {
    @Id
    @Column(name = "guest_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 70)
    private String name;
    @Column(length = 80)
    private String patronymic;
    @Column(length = 70)
    private String lastname;
    private LocalDateTime dateRegistration;
    @Column(length = 12)
    private String phone;


    @PrePersist
    protected void onCreate() {
        if (dateRegistration == null) {
            dateRegistration = LocalDateTime.now();
        }
    }

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Order> orders = new HashSet<>();
}
