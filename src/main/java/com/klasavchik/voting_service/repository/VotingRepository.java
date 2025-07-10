package com.klasavchik.voting_service.repository;

import com.klasavchik.voting_service.entity.Voting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VotingRepository extends JpaRepository<Voting, String> {
    @Query("SELECT v FROM Voting v WHERE v.id = :id")
    Optional<Voting> findByIdWithOptions(String id);

    @Query("SELECT v FROM Voting v ORDER BY v.creationDate DESC")
    List<Voting> findAllOrderedByCreationDate();

    @Query("SELECT v FROM Voting v ORDER BY v.creationDate DESC LIMIT 15")
    List<Voting> findLast15ByCreationDate();
}