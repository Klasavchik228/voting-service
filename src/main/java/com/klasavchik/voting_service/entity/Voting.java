package com.klasavchik.voting_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "votings")
@Data
@NoArgsConstructor
public class Voting {
    @Id
    @Column(name = "id", length = 66)
    private String id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "creator_id", length = 42, nullable = false)
    private String creatorId;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false;

    @Column(name = "min_votes", nullable = false)
    private int minVotes;

    @Column(name = "end_date", nullable = false)
    private ZonedDateTime endDate;

    @Column(name = "start_date", nullable = false) // Обязательное поле
    private ZonedDateTime startDate;

    @Column(name = "creation_date", nullable = false)
    private ZonedDateTime creationDate;

    @OneToMany(mappedBy = "voting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VotingOption> options;
}