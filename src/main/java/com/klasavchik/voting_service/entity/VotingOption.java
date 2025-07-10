package com.klasavchik.voting_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "voting_options")
@Data
@NoArgsConstructor
public class VotingOption {
    @Embeddable
    public static class VotingOptionId implements Serializable {
        @Column(name = "voting_id", length = 66)
        private String votingId;

        @Column(name = "option_id")
        private String optionId;

        public VotingOptionId() {
        }

        public VotingOptionId(String votingId, String optionId) {
            this.votingId = votingId;
            this.optionId = optionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VotingOptionId that = (VotingOptionId) o;
            return votingId.equals(that.votingId) && optionId.equals(that.optionId);
        }

        @Override
        public int hashCode() {
            return 31 * votingId.hashCode() + optionId.hashCode();
        }

        // Добавляем геттеры для удобства
        public String getVotingId() {
            return votingId;
        }

        public String getOptionId() {
            return optionId;
        }
    }

    @EmbeddedId
    private VotingOptionId id;

    @Column(name = "text", length = 100, nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voting_id", insertable = false, updatable = false)
    private Voting voting;
}