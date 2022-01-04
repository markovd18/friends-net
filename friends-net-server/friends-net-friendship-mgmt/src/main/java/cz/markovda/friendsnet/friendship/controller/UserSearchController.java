package cz.markovda.friendsnet.friendship.controller;

import cz.markovda.api.UserSearchControllerApi;
import cz.markovda.friendsnet.friendship.service.IUserSearchService;
import cz.markovda.friendsnet.friendship.vos.IUserSearchResultVO;
import cz.markovda.vo.EnumRelationshipStatus;
import cz.markovda.vo.UserRelationshipVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
@RestController
@RequiredArgsConstructor
public class UserSearchController implements UserSearchControllerApi {

    private final IUserSearchService userSearchService;

    @Override
    public ResponseEntity<List<UserRelationshipVO>> findBlockedUsers() {
        final List<IUserSearchResultVO> foundUsers = userSearchService.findUsersBlockedToAuthenticatedUser();
        return ResponseEntity.ok(foundUsers.stream()
                .map(this::createUserRelationshipVO)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<List<UserRelationshipVO>> findFriendRequests() {
        final List<IUserSearchResultVO> foundUsers = userSearchService.findPendingRequestsForAuthenticatedUser();
        return ResponseEntity.ok(foundUsers.stream()
                .map(this::createUserRelationshipVO)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<List<UserRelationshipVO>> findFriends() {
        final List<IUserSearchResultVO> foundUsers = userSearchService.findAuthenticatedUsersFriends();
        return ResponseEntity.ok(foundUsers.stream()
                .map(this::createUserRelationshipVO)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<List<UserRelationshipVO>> findUsers(final String nameLike) {
        final List<IUserSearchResultVO> foundUsers = userSearchService.findUsersWithNamesContainingString(nameLike);
        return ResponseEntity.ok(foundUsers.stream()
                .map(this::createUserRelationshipVO)
                .collect(Collectors.toList()));
    }

    private UserRelationshipVO createUserRelationshipVO(final IUserSearchResultVO userSearchResultVO) {
        return new UserRelationshipVO()
                .login(userSearchResultVO.getLogin())
                .name(userSearchResultVO.getName())
                .relationshipStatus(userSearchResultVO.getRelationshipStatus() == null
                        ? null : EnumRelationshipStatus.valueOf(userSearchResultVO.getRelationshipStatus().name()));
    }
}
