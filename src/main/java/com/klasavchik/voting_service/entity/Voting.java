package com.klasavchik.voting_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "votings")
@Data
@NoArgsConstructor
public class Voting {
    @Id
    @Column(name = "id", length = 66)
    private String id; // Transaction hash

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "creator_id", length = 42, nullable = false)
    private String creatorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", insertable = false, updatable = false)
    private User creator;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false;

    @Column(name = "min_votes", nullable = false)
    private int minVotes = 1;

    @Column(name = "end_date", nullable = false)
    private ZonedDateTime endDate;

    @Column(name = "start_date", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime startDate; // Переименовано из created_at

    @Column(name = "creation_date", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private ZonedDateTime creationDate; // Новое поле для даты создания

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "voting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VotingOption> options = new ArrayList<>();
}