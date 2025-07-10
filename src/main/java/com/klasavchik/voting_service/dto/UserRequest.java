package com.klasavchik.voting_service.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String userId; // Переименовано с id на userId для соответствия входящему JSON
}