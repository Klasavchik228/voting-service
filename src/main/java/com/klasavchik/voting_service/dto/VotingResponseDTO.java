package com.klasavchik.voting_service.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class VotingResponseDTO {
    private String votingId;
    private String title;
    private String description;
    private String creatorId;
    private boolean isPrivate;
    private int minVotes;
    private ZonedDateTime endDate;
    private ZonedDateTime startDate; // Переименовано из createdAt
    private ZonedDateTime creationDate; // Новое поле
    private List<VotingOptionDTO> options;
}