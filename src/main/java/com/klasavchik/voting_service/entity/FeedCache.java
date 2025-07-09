package com.klasavchik.voting_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "feed_cache")
@Data
@NoArgsConstructor
public class FeedCache {
    @Embeddable
    public static class FeedCacheId implements Serializable {
        @Column(name = "user_id", length = 42)
        private String userId;

        @Column(name = "voting_id", length = 66)
        private String votingId;

        public FeedCacheId() {}
        public FeedCacheId(String userId, String votingId) {
            this.userId = userId;
            this.votingId = votingId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FeedCacheId that = (FeedCacheId) o;
            return userId.equals(that.userId) && votingId.equals(that.votingId);
        }

        @Override
        public int hashCode() {
            return 31 * userId.hashCode() + votingId.hashCode();
        }
    }

    @EmbeddedId
    private FeedCacheId id;

    @Column(name = "score", nullable = false)
    private float score;

    @Column(name = "expires_at", nullable = false)
    private ZonedDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voting_id", insertable = false, updatable = false)
    private Voting voting;
}