package cz.markovda.friendsnet.friendship.repository;

import cz.markovda.friendsnet.friendship.dos.IUserRelationshipDO;
import cz.markovda.friendsnet.friendship.dos.IUserSearchResultDO;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public interface IUserRelationshipRepository {

    void saveNewRelationship(@NotNull IUserRelationshipDO relationshipDO);

    boolean relationshipExists(int senderId, int receiverId);

    List<IUserSearchResultDO> findNonFriendUsers_withNotBlockedRelationship_withNameLike(@NotNull String authenticatedUser,
                                                                                         @NotNull String searchString);

    List<IUserSearchResultDO> findBlockedUsersByUser(@NotNull String username);

    List<IUserSearchResultDO> findUsersFriends(@NotNull String username);

    List<IUserSearchResultDO> findPendingRequests(@NotNull String username);

    void removeRelationship(@NotNull String firstUsername, @NotNull String secondUsername);
}
