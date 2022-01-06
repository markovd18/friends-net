package cz.markovda.friendsnet.friendship.service.impl;

import cz.markovda.friendsnet.auth.service.IAuthenticationService;
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

import java.util.List;
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
    public List<IUserSearchResultVO> findPendingRequestsForUser(String username) {
        log.debug("Start of findPendingRequestsForUser (args: {}).", username);
        Assert.notNull(username, "Username may not be null");
        final List<IUserSearchResultDO> searchResults = userRelationshipRepository.findUsersWithRelationshipToUser(
                username, EnumRelationshipStatus.REQUEST_SENT);
        final List<IUserSearchResultVO> result = createSearchResults(searchResults);
        log.debug("End of findPendingRequestsForUser method. Found {} results.", result.size());
        return result;
    }

    private List<IUserSearchResultVO> createSearchResults(final List<IUserSearchResultDO> searchResults) {
        return searchResults.stream()
                .map(this::createSearchResultVO)
                .collect(Collectors.toList());
    }

    private IUserSearchResultVO createSearchResultVO(final IUserSearchResultDO searchResultDO) {
        final IUserSearchResultVO.EnumRelationshipStatus status = createRelationshipStatusVO(searchResultDO.getRelationshipStatus());
        return new UserSearchResultVO(searchResultDO.getName(), searchResultDO.getLogin(), status);
    }

    private IUserSearchResultVO.EnumRelationshipStatus createRelationshipStatusVO(final EnumRelationshipStatus status) {
        if (status == null) {
            return null;
        }

        return IUserSearchResultVO.EnumRelationshipStatus.valueOf(status.name());
    }
}
