package com.klasavchik.voting_service.kafka;

import com.klasavchik.voting_service.dto.VotingCreateRequest;
import com.klasavchik.voting_service.entity.Voting;
import com.klasavchik.voting_service.entity.VotingOption;
import com.klasavchik.voting_service.repository.VotingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Обработчик создания новых голосований на основе входящих сообщений.
 * Преобразует запрос в сущность и сохраняет её с опциями.
 *
 * @author Андрей Бокарев
 * @version 1.0
 * @see VotingRepository
 */
@Component
public class VotingCreateHandler {
    private static final Logger logger = LoggerFactory.getLogger(VotingCreateHandler.class);
    private final VotingRepository votingRepository;

    /**
     * Конструктор с инъекцией репозитория голосований.
     *
     * @param votingRepository Репозиторий для работы с голосованиями
     */
    public VotingCreateHandler(VotingRepository votingRepository) {
        this.votingRepository = votingRepository;
    }

    /**
     * Обрабатывает запрос на создание нового голосования.
     * Создаёт сущность Voting с опциями и сохраняет её в базе данных.
     *
     * @param request Объект запроса {@link VotingCreateRequest} с данными голосования
     * @throws IllegalArgumentException если startDate отсутствует
     */
    @Transactional
    public void handle(VotingCreateRequest request) {
        logger.info("Обработка нового голосования: {}", request.getId());

        if (request.getStartDate() == null) {
            throw new IllegalArgumentException("startDate не может быть null");
        }

        Voting voting = new Voting();
        voting.setId(request.getId());
        voting.setTitle(request.getTitle());
        voting.setDescription(request.getDescription());
        voting.setCreatorId(request.getCreatorId());
        voting.setPrivate(request.isPrivate());
        voting.setMinVotes(request.getMinVotes());
        voting.setEndDate(request.getEndDate());
        voting.setStartDate(request.getStartDate()); // Устанавливаем startDate из запроса
        voting.setCreationDate(ZonedDateTime.now()); // Устанавливаем дату создания сами

        List<VotingOption> options = request.getOptions().stream()
                .map(option -> {
                    VotingOption votingOption = new VotingOption();
                    votingOption.setId(new VotingOption.VotingOptionId(request.getId(), option.getOptionId()));
                    votingOption.setText(option.getText());
                    votingOption.setVoting(voting);
                    return votingOption;
                })
                .collect(Collectors.toList());

        voting.setOptions(options);
        votingRepository.save(voting);
    }
}