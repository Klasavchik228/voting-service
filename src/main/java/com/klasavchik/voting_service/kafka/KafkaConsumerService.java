package com.klasavchik.voting_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.klasavchik.voting_service.dto.*;
import com.klasavchik.voting_service.entity.Vote;
import com.klasavchik.voting_service.entity.Voting;
import com.klasavchik.voting_service.kafka.UserRegistrationHandler;
import com.klasavchik.voting_service.repository.VoteRepository;
import com.klasavchik.voting_service.repository.VotingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
        * Сервис для обработки входящих сообщений из Kafka и отправки ответов.
        * Обрабатывает запросы на регистрацию пользователей, создание голосований, регистрацию голосов,
        * получение истории голосований, списка всех голосований и голосований пользователя.
        *
        * @author Андрей Бокарев
        * @version 1.0
        * @see VotingRepository
 * @see VoteRepository
 * @see UserRegistrationHandler
 * @see VotingCreateHandler
 * @see VoteCastHandler
 */
@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final UserRegistrationHandler userRegistrationHandler;
    private final VotingCreateHandler votingCreateHandler;
    private final VoteCastHandler voteCastHandler;
    private final VoteRepository voteRepository;
    private final VotingRepository votingRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Конструктор сервиса с инъекцией зависимостей.
     *
     * @param userRegistrationHandler Обработчик регистрации пользователей
     * @param votingCreateHandler     Обработчик создания голосований
     * @param voteCastHandler         Обработчик регистрации голосов
     * @param voteRepository          Репозиторий для работы с голосами
     * @param votingRepository        Репозиторий для работы с голосованиями
     * @param kafkaTemplate           Шаблон Kafka для отправки сообщений
     */
    public KafkaConsumerService(UserRegistrationHandler userRegistrationHandler,
                                VotingCreateHandler votingCreateHandler,
                                VoteCastHandler voteCastHandler,
                                VoteRepository voteRepository,
                                VotingRepository votingRepository,
                                KafkaTemplate<String, String> kafkaTemplate) {
        this.userRegistrationHandler = userRegistrationHandler;
        this.votingCreateHandler = votingCreateHandler;
        this.voteCastHandler = voteCastHandler;
        this.voteRepository = voteRepository;
        this.votingRepository = votingRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Обрабатывает запрос на регистрацию нового пользователя из топика 'user-registrations'.
     *
     * @param message JSON-строка с данными запроса {@link UserRequest}
     */
    @KafkaListener(topics = "user-registrations", groupId = "voting-group")
    public void listenForUserRegistration(String message) {
        try {
            UserRequest userRequest = objectMapper.readValue(message, UserRequest.class);
            userRegistrationHandler.handle(userRequest);
        } catch (Exception e) {
            logger.error("Ошибка при обработке регистрации пользователя", e);
        }
    }
    /**
     * Обрабатывает запрос на создание нового голосования из топика 'voting-create'.
     *
     * @param message JSON-строка с данными запроса {@link VotingCreateRequest}
     */
    @KafkaListener(topics = "voting-create", groupId = "voting-group")
    public void listenForVotingCreate(String message) {
        try {
            logger.info("Получено сообщение для voting-create: {}", message);
            VotingCreateRequest request = objectMapper.readValue(message, VotingCreateRequest.class);
            logger.info("Успешно десериализовано: {}", request);
            votingCreateHandler.handle(request);
        } catch (Exception e) {
            logger.error("Ошибка при обработке голосования", e);
        }
    }
    /**
     * Обрабатывает запрос на регистрацию голоса из топика 'vote-cast'.
     *
     * @param message JSON-строка с данными запроса {@link VoteRequest}
     */
    @KafkaListener(topics = "vote-cast", groupId = "voting-group")
    public void listenForVoteCast(String message) {
        try {
            logger.info("Получено сообщение для vote-cast: {}", message);
            VoteRequest request = objectMapper.readValue(message, VoteRequest.class);
            logger.info("Успешно десериализовано: {}", request);
            voteCastHandler.handle(request);
        } catch (Exception e) {
            logger.error("Ошибка при обработке голоса", e);
        }
    }
    /**
     * Обрабатывает запрос истории голосований из топика 'vote-history-request'.
     * Возвращает последние 30 голосов пользователя с информацией о голосовании.
     *
     * @param message JSON-строка с данными запроса {@link VoteHistoryRequest}
     * @throws IllegalArgumentException если голосование не найдено
     */
    @Transactional
    @KafkaListener(topics = "vote-history-request", groupId = "voting-group")
    public void listenForVoteHistoryRequest(String message) {
        try {
            logger.info("Получен запрос истории голосований: {}", message);
            VoteHistoryRequest request = objectMapper.readValue(message, VoteHistoryRequest.class);

            // Подсчёт количества проголосовавших для каждого votingId
            List<Object[]> votersCounts = voteRepository.countVotersByVotingId();
            Map<String, Integer> votersCountMap = new HashMap<>();
            for (Object[] result : votersCounts) {
                votersCountMap.put((String) result[0], ((Number) result[1]).intValue()); // votingId, votersCount
            }

            // Получаем последние 30 записей истории голосований для пользователя
            List<Vote> votes = voteRepository.findLast30ByVoterIdWithVoting(request.getUserId());

            List<VotingHistoryDTO> history = votes.stream()
                    .map(vote -> {
                        VotingHistoryDTO dto = new VotingHistoryDTO();
                        dto.setIsPrivate(vote.getVoting().isPrivate());

                        Voting voting = votingRepository.findByIdWithOptions(vote.getId().getVotingId())
                                .orElseThrow(() -> new IllegalArgumentException("Голосование не найдено: " + vote.getId().getVotingId()));
                        dto.setTitle(voting.getTitle());

                        voting.getOptions().stream()
                                .filter(option -> option.getId().getOptionId().equals(vote.getOptionId()))
                                .findFirst()
                                .ifPresent(option -> dto.setOptionText(option.getText()));

                        // Устанавливаем количество проголосовавших для этого голосования
                        dto.setVotersCount(votersCountMap.getOrDefault(vote.getId().getVotingId(), 0));

                        return dto;
                    })
                    .collect(Collectors.toList());

            VotingHistoryResponse response = new VotingHistoryResponse();
            response.setUserId(request.getUserId());
            response.setHistory(history);

            String responseJson = objectMapper.writeValueAsString(response);
            kafkaTemplate.send("vote-history-response", responseJson);
            logger.info("Отправлен ответ с историей голосований (последние 30): {}", responseJson);
        } catch (Exception e) {
            logger.error("Ошибка при обработке запроса истории голосований", e);
        }
    }

    /**
     * Обрабатывает запрос информации о голосовании из топика 'voting-request'.
     * Возвращает детали голосования, включая статистику голосов.
     *
     * @param message JSON-строка с данными запроса {@link VotingRequest}
     * @throws IllegalArgumentException если голосование не найдено
     */
    @Transactional
    @KafkaListener(topics = "voting-request", groupId = "voting-group")
    public void listenForVotingRequest(String message) {
        try {
            logger.info("Получен запрос информации о голосовании: {}", message);
            VotingRequest request = objectMapper.readValue(message, VotingRequest.class);

            Voting voting = votingRepository.findByIdWithOptions(request.getVotingId())
                    .orElseThrow(() -> new IllegalArgumentException("Голосование не найдено: " + request.getVotingId()));

            // Подсчёт голосов за каждый вариант
            List<Object[]> voteCounts = voteRepository.countVotesByOptionId(voting.getId());
            Map<String, Integer> voteCountMap = new HashMap<>();
            for (Object[] result : voteCounts) {
                voteCountMap.put((String) result[1], ((Number) result[2]).intValue()); // optionId, voteCount
            }

            // Подсчёт общего количества уникальных проголосовавших
            List<Object[]> votersCounts = voteRepository.countVotersByVotingId();
            int totalVoters = votersCounts.stream()
                    .filter(result -> ((String) result[0]).equals(voting.getId()))
                    .findFirst()
                    .map(result -> ((Number) result[1]).intValue())
                    .orElse(0);

            VotingResponseDTO responseDTO = new VotingResponseDTO();
            responseDTO.setTitle(voting.getTitle());
            responseDTO.setDescription(voting.getDescription());
            responseDTO.setCreatorId(voting.getCreatorId());
            responseDTO.setMinVotes(voting.getMinVotes());
            responseDTO.setEndDate(voting.getEndDate());
            responseDTO.setStartDate(voting.getStartDate());
            responseDTO.setVoteCount(totalVoters);// Обновлено
            responseDTO.setOptions(voting.getOptions().stream()
                    .map(option -> {
                        VotingOptionDTO optionDTO = new VotingOptionDTO();
                        optionDTO.setOptionId(option.getId().getOptionId());
                        optionDTO.setText(option.getText());
                        optionDTO.setVoteCount(voteCountMap.getOrDefault(option.getId().getOptionId(), 0));
                        return optionDTO;
                    })
                    .collect(Collectors.toList()));

            String responseJson = objectMapper.writeValueAsString(responseDTO);
            kafkaTemplate.send("voting-response", responseJson);
            logger.info("Отправлен ответ с информацией о голосовании: {}", responseJson);
        } catch (Exception e) {
            logger.error("Ошибка при обработке запроса информации о голосовании", e);
        }
    }
    /**
     * Обрабатывает запрос списка всех голосований из топика 'trigger-all-votings'.
     * Возвращает последние 15 голосований, отсортированных по дате создания.
     *
     * @param message Триггерное сообщение
     */
    @KafkaListener(topics = "trigger-all-votings", groupId = "voting-group")
    @Transactional
    public void listenForTriggerAllVotings(String message) {
        try {
            logger.info("Получен триггер для отправки списка всех голосований: {}", message);

            // Получаем последние 15 голосований, отсортированных по creationDate (от нового к старому)
            List<Voting> votings = votingRepository.findLast15ByCreationDate();

            // Преобразуем в DTO
            GetAllVotingsByCreationDateDTO responseDTO = new GetAllVotingsByCreationDateDTO();
            responseDTO.setVotings(votings.stream().map(voting -> {
                GetAllVotingsByCreationDateDTO.VotingSummaryByCreationDTO dto = new GetAllVotingsByCreationDateDTO.VotingSummaryByCreationDTO();
                dto.setId(voting.getId());
                dto.setTitle(voting.getTitle());
                dto.setDescription(voting.getDescription());
                dto.setStartDate(voting.getStartDate());
                dto.setEndDate(voting.getEndDate());
                return dto;
            }).collect(Collectors.toList()));

            String responseJson = objectMapper.writeValueAsString(responseDTO);
            kafkaTemplate.send("all-votings-response", responseJson);
            logger.info("Отправлен список последних 15 голосований: {}", responseJson);
        } catch (Exception e) {
            logger.error("Ошибка при обработке триггера для отправки всех голосований", e);
        }
    }
    /**
     * Обрабатывает запрос голосований пользователя из топика 'trigger-user-votings-request'.
     * Возвращает последние 15 голосований, созданных пользователем.
     *
     * @param message JSON-строка с данными запроса {@link UserVotingsRequest}
     */
    @Transactional
    @KafkaListener(topics = "trigger-user-votings-request", groupId = "voting-group")
    public void listenForUserVotingsRequest(String message) {
        try {
            logger.info("Получен запрос голосований пользователя: {}", message);
            UserVotingsRequest request = objectMapper.readValue(message, UserVotingsRequest.class);

            // Получаем последние 15 голосований, созданных пользователем
            List<Voting> votings = votingRepository.findLast15ByCreatorId(request.getCreatorId());

            // Преобразуем в DTO
            GetAllVotingsByCreationDateDTO responseDTO = new GetAllVotingsByCreationDateDTO();
            responseDTO.setVotings(votings.stream().map(voting -> {
                GetAllVotingsByCreationDateDTO.VotingSummaryByCreationDTO dto = new GetAllVotingsByCreationDateDTO.VotingSummaryByCreationDTO();
                dto.setId(voting.getId());
                dto.setTitle(voting.getTitle());
                dto.setDescription(voting.getDescription());
                dto.setStartDate(voting.getStartDate());
                dto.setEndDate(voting.getEndDate());
                return dto;
            }).collect(Collectors.toList()));

            String responseJson = objectMapper.writeValueAsString(responseDTO);
            kafkaTemplate.send("user-votings-response", responseJson);
            logger.info("Отправлен список последних 15 голосований пользователя: {}", responseJson);
        } catch (Exception e) {
            logger.error("Ошибка при обработке запроса голосований пользователя", e);
        }
    }
}