package com.klasavchik.voting_service.dto;

import lombok.Data;

@Data
public class UserVotingsRequest {
    private String creatorId; // Идентификатор создателя (Ethereum address)
}