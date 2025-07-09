package com.klasavchik.voting_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "id", length = 42)
    private String id; // Ethereum address

    @Column(name = "username", length = 64, unique = true)
    private String username;

    @Column(name = "registered_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private ZonedDateTime registeredAt;
}