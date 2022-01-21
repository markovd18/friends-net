package cz.markovda.friendsnet.friendship.service;

import cz.markovda.friendsnet.auth.vos.IUserWithRolesSearchResultVO;
import cz.markovda.friendsnet.friendship.vos.IUserSearchResultVO;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public interface IUserSearchService {

    List<IUserSearchResultVO> findUsersWithNamesContainingString(@NotNull String searchString);

    List<IUserSearchResultVO> findUsersBlockedToAuthenticatedUser();

    List<IUserSearchResultVO> findAuthenticatedUsersFriends();

    List<IUserSearchResultVO> findUsersBlockedToUser(@NotNull String username);

    List<IUserSearchResultVO> findUsersFriends(@NotNull String username);

    List<IUserSearchResultVO> findPendingRequestsForAuthenticatedUser();

    List<IUserSearchResultVO> findPendingRequestsForUser(@NotNull String username);

    List<IUserWithRolesSearchResultVO> findAuthenticatedUsersFriendsWithRoles();

    List<IUserWithRolesSearchResultVO> findUsersFriendsWithRoles(@NotNull String username);
}
