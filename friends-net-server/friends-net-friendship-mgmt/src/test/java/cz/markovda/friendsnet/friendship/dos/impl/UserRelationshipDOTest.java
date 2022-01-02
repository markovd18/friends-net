package cz.markovda.friendsnet.friendship.dos.impl;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public class UserRelationshipDOTest {

    @Test
    public void constructorFillsAllAttributesCorrectly() {
        final var senderId = 2;
        final var receiverId = 5;
        final var createdAt = LocalDateTime.now();

        final UserRelationshipDO relationshipDO = new UserRelationshipDO(senderId, receiverId, createdAt);
        assertAll(() -> assertEquals(senderId, relationshipDO.getSenderId(),
                        "Sender ID should match the constructor argument!"),
                () -> assertEquals(receiverId, relationshipDO.getReceiverId(),
                        "Receiver ID should match the constructor argument!"),
                () -> assertEquals(createdAt, relationshipDO.getCreatedAt(),
                        "Creation time should match the constructor argument!"),
                () -> assertEquals(createdAt, relationshipDO.getLastUpdated(),
                        "Last update time of new relationship should match the creation time!"),
                () -> assertEquals(EnumRelationshipStatus.REQUEST_SENT, relationshipDO.getRelationshipStatus(),
                        "Initial relationship status should be " + EnumRelationshipStatus.REQUEST_SENT.name()));

    }
}
