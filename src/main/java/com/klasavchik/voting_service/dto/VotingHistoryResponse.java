package com.klasavchik.voting_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class VotingHistoryResponse {
    private String userId;
    private List<VotingHistoryDTO> history;
}