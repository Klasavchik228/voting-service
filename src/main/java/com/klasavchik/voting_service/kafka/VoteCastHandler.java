package com.klasavchik.voting_service.kafka;

import com.klasavchik.voting_service.dto.VoteRequest;
import com.klasavchik.voting_service.entity.Vote;
import com.klasavchik.voting_service.entity.Vote.VoteId;
import com.klasavchik.voting_service.repository.VoteRepository;
import com.klasavchik.voting_service.repository.VotingRepository;
import com.klasavchik.voting_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class VoteCastHandler {
    private static final Logger logger = LoggerFactory.getLogger(VoteCastHandler.class);
    private final VoteRepository voteRepository;
    private final VotingRepository votingRepository;
    private final UserRepository userRepository;

    public VoteCastHandler(VoteRepository voteRepository, VotingRepository votingRepository, UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.votingRepository = votingRepository;
        this.userRepository = userRepository;
    }

    public void handle(VoteRequest request) {
        logger.info("Обработка голоса для votingId: {}, voterId: {}", request.getVotingId(), request.getVoterId());

        // Проверка существования голосования
        votingRepository.findById(request.getVotingId())
                .orElseThrow(() -> new IllegalArgumentException("Голосование не найдено: " + request.getVotingId()));

        // Проверка существования пользователя
        userRepository.findById(request.getVoterId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + request.getVoterId()));

        // Проверка, не голосовал ли пользователь уже
        voteRepository.findById(new VoteId(request.getVotingId(), request.getVoterId()))
                .ifPresent(vote -> {
                    throw new IllegalStateException("Пользователь уже голосовал в этом голосовании: " + request.getVoterId());
                });

        // Создание и сохранение голоса
        Vote vote = new Vote();
        vote.setId(new VoteId(request.getVotingId(), request.getVoterId()));
        vote.setOptionId(request.getOptionId());
        vote.setCreatedAt(ZonedDateTime.now());

        voteRepository.save(vote);
        logger.info("Голос успешно сохранён: votingId={}, voterId={}, optionId={}",
                request.getVotingId(), request.getVoterId(), request.getOptionId());
    }
}