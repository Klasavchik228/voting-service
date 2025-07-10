package com.klasavchik.voting_service.dto;

import lombok.Data;

@Data
public class VoteRequest {
    private String votingId;
    private String voterId;
    private String optionId;
    // Убран txHash
}