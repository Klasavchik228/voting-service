package com.klasavchik.voting_service.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class VotingCreateRequest {
    private String id;
    private String title;
    private String description;
    private String creatorId;
    private boolean isPrivate;
    private int minVotes;
    private ZonedDateTime endDate;
    private ZonedDateTime startDate; // Обязательное поле
    private List<VotingOptionDTO> options;
}