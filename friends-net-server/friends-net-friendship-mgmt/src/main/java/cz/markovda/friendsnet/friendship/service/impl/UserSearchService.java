package cz.markovda.friendsnet.friendship.service.impl;

import cz.markovda.friendsnet.auth.dos.impl.UserDO;
import cz.markovda.friendsnet.auth.dos.impl.UserRoleDO;
import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.service.IAuthenticationService;
import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.friendsnet.auth.vos.IUserWithRolesSearchResultVO;
import cz.markovda.friendsnet.auth.vos.impl.UserWithRolesSearchResultVO;
import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.projection.IUserSearchResultDO;
import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import cz.markovda.friendsnet.friendship.service.IUserSearchService;
import cz.markovda.friendsnet.friendship.vos.IUserSearchResultVO;
import cz.markovda.friendsnet.friendship.vos.impl.UserSearchResultVO;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserSearchService implements IUserSearchService {

    private final IUserRelationshipRepository userRelationshipRepository;
    private final IUserRepository userRepository;
    private final IAuthenticationService authenticationService;

    @Override
    public List<IUserSearchResultVO> findUsersWithNamesContainingString(final String searchString) {
        log.debug("Start of findUsersWithNamesContainingString method (args: {})", searchString);
        Assert.notNull(searchString, "Search string may not be null!");
        final var authUserLogin = authenticationService.getLoginName();
        final String preparedSearchString = "%" + searchString + "%";
        final List<IUserSearchResultDO> foundUsers = userRelationshipRepository.findPotentialFriendsWithNameLike(authUserLogin, preparedSearchString);
        final List<IUserSearchResultVO> result = createSearchResults(foundUsers);
        log.debug("End of findUsersWithNamesContainingString method. Found {} results.", result.size());
        return result;
    }

    @Override
    public List<IUserSearchResultVO> findUsersBlockedToAuthenticatedUser() {
        log.debug("Start of findUsersBlockedToAuthenticatedUser method.");
        final String username = authenticationService.getLoginName();
        final List<IUserSearchResultVO> result = findUsersBlockedToUser(username);
        log.debug("End of findUsersBlockedToAuthenticatedUser method.");
        return result;
    }

    @Override
    public List<IUserSearchResultVO> findAuthenticatedUsersFriends() {
        log.debug("Start of findAuthenticatedUsersFriends method.");
        final String username = authenticationService.getLoginName();
        final List<IUserSearchResultVO> result =  findUsersFriends(username);
        log.debug("End of findAuthenticatedUsersFriends method.");
        return result;
    }

    @Override
    public List<IUserSearchResultVO> findUsersBlockedToUser(final String username) {
        log.debug("Start of findUsersBlockedToUser method (args: {}).", username);
        Assert.notNull(username, "Username may not be null");
        final List<IUserSearchResultDO> searchResults = userRelationshipRepository.findUsersWithRelationshipToUser(
                username, EnumRelationshipStatus.BLOCKED);
        final List<IUserSearchResultVO> result = createSearchResults(searchResults);
        log.debug("End of findUsersBlockedToUser method. Found {} results.", result.size());
        return result;
    }

    @Override
    public List<IUserSearchResultVO> findUsersFriends(final String username) {
        log.debug("Start of findUsersFriends method (args: {}).", username);
        Assert.notNull(username, "Username may not be null");
        final List<IUserSearchResultDO> searchResults = userRelationshipRepository.findUsersFriends(username);
        final List<IUserSearchResultVO> result = createSearchResults(searchResults);
        log.debug("End of findUsersFriends method. Found {} results.", result.size());
        return result;
    }

    @Override
    public List<IUserSearchResultVO> findPendingRequestsForAuthenticatedUser() {
        log.debug("Start of findPendingRequestsForAuthenticatedUser method.");
        final String username = authenticationService.getLoginName();
        final List<IUserSearchResultVO> result = findPendingRequestsForUser(username);
        log.debug("End of findPendingRequestsForAuthenticatedUser method.");
        return result;
    }

    @Override
    public List<IUserSearchResultVO> findPendingRequestsForUser(@NotNull final String username) {
        log.debug("Start of findPendingRequestsForUser (args: {}).", username);
        Assert.notNull(username, "Username may not be null");
        final List<IUserSearchResultDO> searchResults = userRelationshipRepository.findUsersWithRelationshipToUser(
                username, EnumRelationshipStatus.REQUEST_SENT);
        final List<IUserSearchResultVO> result = createSearchResults(searchResults);
        log.debug("End of findPendingRequestsForUser method. Found {} results.", result.size());
        return result;
    }

    @Override
    public List<IUserWithRolesSearchResultVO> findAuthenticatedUsersFriendsWithRoles() {
        log.debug("Start of findAuthenticatedUsersFriendsWithRoles method.");
        final String username = authenticationService.getLoginName();
        final List<IUserWithRolesSearchResultVO> result = findUsersFriendsWithRoles(username);
        log.debug("End of findAuthenticatedUsersFriendsWithRoles method. Found {} results.", result.size());
        return result;
    }

    @Override
    public List<IUserWithRolesSearchResultVO> findUsersFriendsWithRoles(@NotNull final String username) {
        log.debug("Start of findUsersFriendsWithRoles method (args: {}).", username);
        Assert.notNull(username, "Username may not be null");
        final List<String> friendUsernames = userRelationshipRepository.findUsersFriends(username)
                .stream()
                .map(IUserSearchResultDO::getLogin)
                .collect(Collectors.toList());
        final List<UserDO> foundUsers = userRepository.findByLoginInFetchRoles(friendUsernames);
        final List<IUserWithRolesSearchResultVO> result = createSearchWithRolesResults(foundUsers);
        log.debug("End of findUsersFriendsWithRoles method. Found {} results.", result.size());
        return result;
    }

    private List<IUserSearchResultVO> createSearchResults(final List<IUserSearchResultDO> searchResults) {
        return searchResults.stream()
                .map(this::createSearchResultVO)
                .collect(Collectors.toList());
    }

    private List<IUserWithRolesSearchResultVO> createSearchWithRolesResults(final List<UserDO> searchResults) {
        return searchResults.stream()
                .map(this::createSearchWithRolesResultVO)
                .collect(Collectors.toList());
    }

    private IUserSearchResultVO createSearchResultVO(final IUserSearchResultDO searchResultDO) {
        final IUserSearchResultVO.EnumRelationshipStatus status = createRelationshipStatusVO(searchResultDO.getRelationshipStatus());
        return new UserSearchResultVO(searchResultDO.getName(), searchResultDO.getLogin(), status);
    }

    private IUserWithRolesSearchResultVO createSearchWithRolesResultVO(final UserDO searchResult) {
        final Set<IUserVO.EnumUserRole> roles = searchResult.getRoles().stream()
                .map(UserRoleDO::getName)
                .map(Enum::name)
                .map(IUserVO.EnumUserRole::valueOf)
                .collect(Collectors.toSet());
        return new UserWithRolesSearchResultVO(searchResult.getLogin(), searchResult.getName(), roles);
    }

    private IUserSearchResultVO.EnumRelationshipStatus createRelationshipStatusVO(final EnumRelationshipStatus status) {
        if (status == null) {
            return null;
        }

        return IUserSearchResultVO.EnumRelationshipStatus.valueOf(status.name());
    }
}
