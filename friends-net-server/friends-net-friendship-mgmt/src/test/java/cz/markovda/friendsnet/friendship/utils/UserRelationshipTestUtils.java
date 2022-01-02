package cz.markovda.friendsnet.friendship.utils;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.IUserRelationshipDO;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public class UserRelationshipTestUtils {

    public static IUserRelationshipDO prepareNewFriendRequest(final int senderId,
                                                              final int receiverId,
                                                              final LocalDateTime createdAt) {
        return new IUserRelationshipDO() {

            @Override
            public int getSenderId() {
                return senderId;
            }

            @Override
            public int getReceiverId() {
                return receiverId;
            }

            @Override
            public EnumRelationshipStatus getRelationshipStatus() {
                return EnumRelationshipStatus.REQUEST_SENT;
            }

            @Override
            public LocalDateTime getCreatedAt() {
                return createdAt;
            }

            @Override
            public LocalDateTime getLastUpdated() {
                return createdAt;
            }
        };
    }
}
