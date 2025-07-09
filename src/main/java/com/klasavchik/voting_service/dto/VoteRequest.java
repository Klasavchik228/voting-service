package com.klasavchik.voting_service.dto;

import lombok.Data;

@Data
public class VoteRequest {
    private String votingId;
    private String voterId;
    private short optionId;
    private String txHash; // Для уникальности транзакции
}