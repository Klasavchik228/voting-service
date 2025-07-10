package com.klasavchik.voting_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.klasavchik.voting_service.dto.*;
import com.klasavchik.voting_service.entity.Voting;
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

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final com.klasavchik.voting_service.kafka.consumer.UserRegistrationHandler userRegistrationHandler;
    private final VotingCreateHandler votingCreateHandler;
    private final VoteCastHandler voteCastHandler;
    private final VoteRepository voteRepository;
    private final VotingRepository votingRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaConsumerService(com.klasavchik.voting_service.kafka.consumer.UserRegistrationHandler userRegistrationHandler,
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

    @KafkaListener(topics = "user-registrations", groupId = "voting-group")
    public void listenForUserRegistration(String message) {
        try {
            UserRequest userRequest = objectMapper.readValue(message, UserRequest.class);
            userRegistrationHandler.handle(userRequest);
        } catch (Exception e) {
            logger.error("Ошибка при обработке регистрации пользователя", e);
        }
    }

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

            List<VotingHistoryDTO> history = voteRepository.findAllByVoterIdWithVoting(request.getUserId()).stream()
                    .map(vote -> {
                        VotingHistoryDTO dto = new VotingHistoryDTO();
                        dto.setVotingId(vote.getId().getVotingId());
                        dto.setCastAt(vote.getCreatedAt());
                        dto.setOptionId(vote.getOptionId());

                        Voting voting = votingRepository.findByIdWithOptions(vote.getId().getVotingId())
                                .orElseThrow(() -> new IllegalArgumentException("Голосование не найдено: " + vote.getId().getVotingId()));
                        dto.setTitle(voting.getTitle());

                        voting.getOptions().stream()
                                .filter(option -> option.getId().getOptionId() == vote.getOptionId())
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
            logger.info("Отправлен ответ с историей голосований: {}", responseJson);
        } catch (Exception e) {
            logger.error("Ошибка при обработке запроса истории голосований", e);
        }
    }

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
            Map<Short, Integer> voteCountMap = new HashMap<>();
            for (Object[] result : voteCounts) {
                voteCountMap.put((Short) result[1], ((Number) result[2]).intValue()); // optionId, voteCount
            }

            VotingResponseDTO responseDTO = new VotingResponseDTO();
            responseDTO.setVotingId(voting.getId());
            responseDTO.setTitle(voting.getTitle());
            responseDTO.setDescription(voting.getDescription());
            responseDTO.setCreatorId(voting.getCreatorId());
            responseDTO.setPrivate(voting.isPrivate());
            responseDTO.setMinVotes(voting.getMinVotes());
            responseDTO.setEndDate(voting.getEndDate());
            responseDTO.setStartDate(voting.getStartDate()); // Обновлено
            responseDTO.setCreationDate(voting.getCreationDate()); // Новое поле
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
    @KafkaListener(topics = "trigger-all-votings", groupId = "voting-group")
    @Transactional
    public void listenForTriggerAllVotings(String message) {
        try {
            logger.info("Получен триггер для отправки списка всех голосований: {}", message);

            // Получаем все голосования, отсортированные по startDate (от нового к старому)
            List<Voting> votings = votingRepository.findAllOrderedByCreatedAt();

            // Преобразуем в DTO
            GetAllVotingsByCreationDateDTO responseDTO = new GetAllVotingsByCreationDateDTO();
            responseDTO.setVotings(votings.stream().map(voting -> {
                GetAllVotingsByCreationDateDTO.VotingSummaryByCreationDTO dto = new GetAllVotingsByCreationDateDTO.VotingSummaryByCreationDTO();
                dto.setId(voting.getId());
                dto.setTitle(voting.getTitle());
                dto.setDescription(voting.getDescription());
                dto.setStartDate(voting.getStartDate());
                dto.setEndDate(voting.getEndDate());
                dto.setCreationDate(voting.getCreationDate());
                return dto;
            }).collect(Collectors.toList()));

            String responseJson = objectMapper.writeValueAsString(responseDTO);
            kafkaTemplate.send("all-votings-response", responseJson);
            logger.info("Отправлен список всех голосований: {}", responseJson);
        } catch (Exception e) {
            logger.error("Ошибка при обработке триггера для отправки всех голосований", e);
        }
    }
}