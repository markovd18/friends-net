package cz.markovda.friendsnet.friendship.dos.impl;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.IUserRelationshipDO;
import lombok.Getter;

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
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.relationshipStatus = EnumRelationshipStatus.REQUEST_SENT;
        this.createdAt = createdAt;
        this.lastUpdated = createdAt;
    }
}
