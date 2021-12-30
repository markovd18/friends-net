package cz.markovda.friendsnet.friendship.dos;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public interface IDOFactory {

    IUserRelationshipDO createUserRelationship(int senderId, int receiverId, @NotNull LocalDateTime createdAt);
}
