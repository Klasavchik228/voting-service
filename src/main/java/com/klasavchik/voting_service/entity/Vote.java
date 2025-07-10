package com.klasavchik.voting_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "votes")
@Data
@NoArgsConstructor
public class Vote {
    @Embeddable
    @Data
    public static class VoteId implements Serializable {
        @Column(name = "voting_id", length = 66)
        private String votingId;

        @Column(name = "voter_id", length = 42)
        private String voterId;

        public VoteId() {}
        public VoteId(String votingId, String voterId) {
            this.votingId = votingId;
            this.voterId = voterId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VoteId that = (VoteId) o;
            return votingId.equals(that.votingId) && voterId.equals(that.voterId);
        }

        @Override
        public int hashCode() {
            return 31 * votingId.hashCode() + voterId.hashCode();
        }
    }

    @EmbeddedId
    private VoteId id;

    @Column(name = "option_id", nullable = false)
    private String optionId;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private ZonedDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voting_id", insertable = false, updatable = false)
    private Voting voting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", insertable = false, updatable = false)
    private User voter;
}