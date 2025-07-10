package com.klasavchik.voting_service.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class VotingHistoryDTO {
//    private String votingId;
//    private ZonedDateTime castAt;
//    private String optionId;
    private String title;  //+
    private String optionText;//+
    private int votersCount; //+
    private Boolean IsPrivate;
}