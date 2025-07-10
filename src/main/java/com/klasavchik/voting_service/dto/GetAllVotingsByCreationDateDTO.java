package com.klasavchik.voting_service.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class GetAllVotingsByCreationDateDTO {
    private List<VotingSummaryByCreationDTO> votings;

    @Data
    public static class VotingSummaryByCreationDTO {
        private String id;
        private String title;
        private String description;
        private ZonedDateTime startDate;
        private ZonedDateTime endDate;
        //private ZonedDateTime creationDate;
    }
}