package com.klasavchik.voting_service.kafka;

import com.klasavchik.voting_service.dto.UserRequest;
import com.klasavchik.voting_service.entity.User;
import com.klasavchik.voting_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Обработчик регистрации новых пользователей на основе входящих сообщений.
 * Проверяет существование пользователя и регистрирует нового, если его нет.
 *
 * @author Андрей Бокарев
 * @version 1.0
 * @see UserRepository
 */
@Component
public class UserRegistrationHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationHandler.class);

    private final UserRepository userRepository;

    /**
     * Конструктор с инъекцией репозитория пользователей.
     *
     * @param userRepository Репозиторий для работы с пользователями
     */
    public UserRegistrationHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Обрабатывает запрос на регистрацию пользователя.
     * Если пользователь с указанным ID не существует, создаёт нового и сохраняет его.
     *
     * @param request Объект запроса {@link UserRequest} с данными пользователя
     */
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