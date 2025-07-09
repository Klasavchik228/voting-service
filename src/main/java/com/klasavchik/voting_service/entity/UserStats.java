package com.klasavchik.voting_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class UserStats {
    @Id
    @Column(name = "id", length = 42)
    private String id;

    @Column(name = "created_votings")
    private Long createdVotings;

    @Column(name = "participated_votings")
    private Long participatedVotings;

    @Column(name = "subscriptions_count")
    private Long subscriptionsCount;
}