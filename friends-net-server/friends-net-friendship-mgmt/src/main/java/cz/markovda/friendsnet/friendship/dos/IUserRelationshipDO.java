package cz.markovda.friendsnet.friendship.dos;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public interface IUserRelationshipDO {

    int getSenderId();

    int getReceiverId();

    EnumRelationshipStatus getRelationshipStatus();

    LocalDateTime getCreatedAt();

    LocalDateTime getLastUpdated();

    void acceptRequest(@NotNull LocalDateTime acceptedAt);

    void block(@NotNull LocalDateTime blockedAt);
}
