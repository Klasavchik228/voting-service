package com.klasavchik.voting_service.repository;

import com.klasavchik.voting_service.entity.VotingOption;
import com.klasavchik.voting_service.entity.VotingOption.VotingOptionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingOptionRepository extends JpaRepository<VotingOption, VotingOptionId> {}
