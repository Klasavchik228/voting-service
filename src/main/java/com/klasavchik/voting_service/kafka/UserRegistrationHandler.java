package com.klasavchik.voting_service.kafka.consumer;

import com.klasavchik.voting_service.dto.UserRequest;
import com.klasavchik.voting_service.entity.User;
import com.klasavchik.voting_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Optional;

@Component
public class UserRegistrationHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationHandler.class);

    private final UserRepository userRepository;

    public UserRegistrationHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void handle(UserRequest request) {
        logger.info("Обработка регистрации пользователя: {}", request);

        String userId = request.getUserId(); // Изменено с getId() на getUserId()
        Optional<User> existingUser = userRepository.findById(userId);

        if (!existingUser.isPresent()) {
            User user = new User();
            user.setId(userId); // Сохраняем как id в сущности User
            user.setRegisteredAt(ZonedDateTime.now());
            userRepository.save(user);
            logger.info("Зарегистрирован новый пользователь с id: {}", userId);
        } else {
            logger.info("Пользователь с id: {} уже зарегистрирован, пропускаем", userId);
        }
    }
}