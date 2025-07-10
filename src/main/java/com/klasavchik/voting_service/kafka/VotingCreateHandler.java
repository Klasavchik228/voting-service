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

@Component
public class VotingCreateHandler {
    private static final Logger logger = LoggerFactory.getLogger(VotingCreateHandler.class);
    private final VotingRepository votingRepository;

    public VotingCreateHandler(VotingRepository votingRepository) {
        this.votingRepository = votingRepository;
    }

    @Transactional
    public void handle(VotingCreateRequest request) {
        logger.info("Обработка нового голосования: {}", request.getId());

        Voting voting = new Voting();
        voting.setId(request.getId());
        voting.setTitle(request.getTitle());
        voting.setDescription(request.getDescription());
        voting.setCreatorId(request.getCreatorId());
        voting.setPrivate(request.isPrivate());
        voting.setMinVotes(request.getMinVotes());
        voting.setEndDate(request.getEndDate());
        voting.setCreationDate(ZonedDateTime.now());

        // Создаем новые опции голосования
        List<VotingOption> options = request.getOptions().stream()
                .map(option -> {
                    VotingOption votingOption = new VotingOption();
                    votingOption.setId(new VotingOption.VotingOptionId(request.getId(), option.getOptionId()));
                    votingOption.setText(option.getText());
                    votingOption.setVoting(voting); // Устанавливаем связь с голосованием
                    return votingOption;
                })
                .collect(Collectors.toList());

        voting.setOptions(options);
        votingRepository.save(voting);
    }
}