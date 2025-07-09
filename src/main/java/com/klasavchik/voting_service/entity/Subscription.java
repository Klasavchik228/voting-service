package com.klasavchik.voting_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
public class Subscription {
    @Embeddable
    public static class SubscriptionId implements Serializable {
        @Column(name = "user_id", length = 42)
        private String userId;

        @Column(name = "author_id", length = 42)
        private String authorId;

        public SubscriptionId() {}
        public SubscriptionId(String userId, String authorId) {
            this.userId = userId;
            this.authorId = authorId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SubscriptionId that = (SubscriptionId) o;
            return userId.equals(that.userId) && authorId.equals(that.authorId);
        }

        @Override
        public int hashCode() {
            return 31 * userId.hashCode() + authorId.hashCode();
        }
    }

    @EmbeddedId
    private SubscriptionId id;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private ZonedDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    private User author;
}