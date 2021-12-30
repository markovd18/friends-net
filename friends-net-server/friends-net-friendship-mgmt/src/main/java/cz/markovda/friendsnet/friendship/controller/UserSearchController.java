package cz.markovda.friendsnet.friendship.controller;

import cz.markovda.api.UserSearchControllerApi;
import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.friendsnet.friendship.service.IUserSearchService;
import cz.markovda.vo.UserIdentificationDataVO;
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
    public ResponseEntity<List<UserIdentificationDataVO>> findUsers(final String nameLike) {
        final List<IUserVO> foundUsers = userSearchService.findUsersWithNamesContainingString(nameLike);
        return ResponseEntity.ok(foundUsers.stream()
                .map(this::createUserIdentificationData)
                .collect(Collectors.toList()));
    }

    private UserIdentificationDataVO createUserIdentificationData(final IUserVO userVO) {
        return new UserIdentificationDataVO()
                .login(userVO.getLogin())
                .name(userVO.getName());
    }
}
