package com.klasavchik.voting_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VoteHistoryRequest {
    @JsonProperty("userId")  // Явно указываем имя поля в JSON
    private String userId;    // Можно оставить camelCase в Java-коде


}