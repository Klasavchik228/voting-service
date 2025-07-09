package com.klasavchik.voting_service.dto;

import lombok.Data;

@Data
public class VotingOptionDTO {
    private short optionId;
    private String text;
    private int voteCount; // Количество голосов за этот вариант
}