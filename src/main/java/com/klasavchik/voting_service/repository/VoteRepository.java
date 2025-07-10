package com.klasavchik.voting_service.repository;

import com.klasavchik.voting_service.entity.Vote;
import com.klasavchik.voting_service.entity.Vote.VoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface VoteRepository extends JpaRepository<Vote, VoteId> {
    @Query("SELECT v.id.votingId AS votingId, COUNT(DISTINCT v.id.voterId) AS votersCount " +
            "FROM Vote v GROUP BY v.id.votingId")
    List<Object[]> countVotersByVotingId();

    @Query("SELECT v.id.votingId AS votingId, v.optionId AS optionId, COUNT(v) AS voteCount " +
            "FROM Vote v WHERE v.id.votingId = :votingId GROUP BY v.id.votingId, v.optionId")
    List<Object[]> countVotesByOptionId(String votingId);

    @Query("SELECT v FROM Vote v JOIN FETCH v.voting WHERE v.id.voterId = ?1")
    List<Vote> findAllByVoterIdWithVoting(String voterId);

    @Query("SELECT v FROM Vote v JOIN FETCH v.voting WHERE v.id.voterId = ?1 ORDER BY v.createdAt DESC LIMIT 30")
    List<Vote> findLast30ByVoterIdWithVoting(String voterId);
}