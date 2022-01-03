package cz.markovda.friendsnet.friendship.dos.impl;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.IUserRelationshipDO;
import io.jsonwebtoken.lang.Assert;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public class UserRelationshipDO implements IUserRelationshipDO {

    @Getter
    private final int senderId;
    @Getter
    private final int receiverId;
    @Getter
    private EnumRelationshipStatus relationshipStatus;
    @Getter
    private final LocalDateTime createdAt;
    @Getter
    private LocalDateTime lastUpdated;

    protected UserRelationshipDO(final int senderId, final int receiverId, final LocalDateTime createdAt) {
        this(senderId, receiverId, createdAt, createdAt, EnumRelationshipStatus.REQUEST_SENT);
    }

    protected UserRelationshipDO(final int senderId, final int receiverId, final LocalDateTime createdAt,
                                 final LocalDateTime lastUpdated, final EnumRelationshipStatus status) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.relationshipStatus = status;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
    }

    @Override
    public void acceptRequest(@NotNull final LocalDateTime acceptedAt) {
        Assert.notNull(acceptedAt, "Acceptation time may not be null");
        if (relationshipStatus != EnumRelationshipStatus.REQUEST_SENT) {
            throw new IllegalStateException("Only relationship with status REQUEST_SENT may be accepted!");
        }
        Assert.isTrue(acceptedAt.isAfter(lastUpdated), "Acceptation time may not be before last update");

        relationshipStatus = EnumRelationshipStatus.FRIENDS;
        lastUpdated = acceptedAt;
    }

    @Override
    public void block(@NotNull final LocalDateTime blockedAt) {
        Assert.notNull(blockedAt, "Blockage time may not be null");
        Assert.isTrue(blockedAt.isAfter(lastUpdated), "Blockage time may not be before last update");
        relationshipStatus = EnumRelationshipStatus.BLOCKED;
        lastUpdated = blockedAt;
    }
}
