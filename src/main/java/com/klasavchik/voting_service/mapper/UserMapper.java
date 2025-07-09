package com.klasavchik.voting_service.mapper;

import com.klasavchik.voting_service.dto.UserRequest;
import com.klasavchik.voting_service.entity.User;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        User user = new User();
        user.setId(request.getId());
        user.setUsername(request.getUsername());
        user.setRegisteredAt(ZonedDateTime.now());
        return user;
    }
}
