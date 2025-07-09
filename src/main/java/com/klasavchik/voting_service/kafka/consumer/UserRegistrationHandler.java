package com.klasavchik.voting_service.kafka.consumer;

import com.klasavchik.voting_service.dto.UserRequest;
import com.klasavchik.voting_service.entity.User;
import com.klasavchik.voting_service.mapper.UserMapper;
import com.klasavchik.voting_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRegistrationHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationHandler.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserRegistrationHandler(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public void handle(UserRequest request) {
        logger.info("Обработка регистрации пользователя: {}", request);

        Optional<User> existingUser = userRepository.findById(request.getId());

        User user = existingUser.orElseGet(() -> userMapper.toEntity(request));

        // если пользователь уже есть — только обновляем username
        if (existingUser.isPresent()) {
            user.setUsername(request.getUsername());
        }

        userRepository.save(user);
        logger.info("Пользователь сохранён: {}", user.getId());
    }
}
