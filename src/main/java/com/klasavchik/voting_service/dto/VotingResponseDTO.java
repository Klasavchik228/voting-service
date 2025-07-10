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
    //private boolean isPrivate;
    private int minVotes;
    private ZonedDateTime endDate;
    private ZonedDateTime startDate;
    //private ZonedDateTime creationDate;
    private int voteCount;
    private List<VotingOptionDTO> options;
}