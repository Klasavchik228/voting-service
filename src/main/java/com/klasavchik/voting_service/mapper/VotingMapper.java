package com.klasavchik.voting_service.mapper;

import com.klasavchik.voting_service.dto.VotingCreateRequest;
import com.klasavchik.voting_service.dto.VotingOptionDTO;
import com.klasavchik.voting_service.entity.User;
import com.klasavchik.voting_service.entity.Voting;
import com.klasavchik.voting_service.entity.VotingOption;
import com.klasavchik.voting_service.entity.VotingOption.VotingOptionId;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VotingMapper {

    public Voting toVotingEntity(VotingCreateRequest dto, User creator) {
        Voting voting = new Voting();
        voting.setId(dto.getId());
        voting.setTitle(dto.getTitle());
        voting.setDescription(dto.getDescription());
        voting.setCreatorId(dto.getCreatorId());
        voting.setPrivate(dto.isPrivate());
        voting.setMinVotes(dto.getMinVotes());
        voting.setEndDate(dto.getEndDate());
        voting.setCreationDate(ZonedDateTime.now());
        return voting;
    }

    public List<VotingOption> toVotingOptions(VotingCreateRequest dto, Voting voting) {
        return dto.getOptions().stream()
                .map(optionDTO -> {
                    VotingOption option = new VotingOption();
                    option.setId(new VotingOptionId(dto.getId(), optionDTO.getOptionId()));
                    option.setText(optionDTO.getText());
                    option.setVoting(voting);
                    return option;
                })
                .collect(Collectors.toList());
    }
}
