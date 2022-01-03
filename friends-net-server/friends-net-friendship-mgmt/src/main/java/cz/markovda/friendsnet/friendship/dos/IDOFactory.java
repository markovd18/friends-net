package cz.markovda.friendsnet.friendship.dos;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public interface IDOFactory {

    IUserRelationshipDO createUserRelationship(int senderId, int receiverId, @NotNull LocalDateTime createdAt);

    IUserRelationshipDO createUserRelationship(int senderId, int receiverId, @NotNull LocalDateTime createdAt,
                                               @NotNull LocalDateTime updatedAt, @NotNull EnumRelationshipStatus status);

    IUserSearchResultDO createUserSearchResult(@NotNull String name,
                                               @NotNull String login,
                                               @Nullable EnumRelationshipStatus status);
}
