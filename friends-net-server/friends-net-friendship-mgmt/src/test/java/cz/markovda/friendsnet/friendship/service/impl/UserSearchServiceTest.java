package cz.markovda.friendsnet.friendship.service.impl;

import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.service.IAuthenticationService;
import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.projection.IUserSearchResultDO;
import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import cz.markovda.friendsnet.friendship.service.IUserSearchService;
import cz.markovda.friendsnet.friendship.vos.IUserSearchResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public class UserSearchServiceTest {

    private IUserSearchService userSearchService;
    private IUserRelationshipRepository userRelationshipRepository;
    private IAuthenticationService authenticationService;

    @BeforeEach
    public void prepareTest() {
        authenticationService = mock(IAuthenticationService.class);
        userRelationshipRepository = mock(IUserRelationshipRepository.class);

        userSearchService = new UserSearchService(userRelationshipRepository, mock(IUserRepository.class), authenticationService);
    }

    @Test
    public void returnsNonNullList_whenNonNullStringPassed() {
        final var searchString = "search";

        when(userRelationshipRepository.findPotentialFriendsWithNameLike("Anonymous", searchString))
                .thenReturn(new ArrayList<>());

        assertNotNull(userSearchService.findUsersWithNamesContainingString(searchString),
                "User search should always return non null list!");
    }

    @Test
    public void returnsNonEmptyList_whenUsersFound() {
        final var searchString = "good search";
        final var authUser = "Dwight";
        final IUserSearchResultDO foundUser = prepareSearchResultDO();
        final IUserSearchResultVO searchResultVO = prepareSearchResultVO(foundUser);

        when(authenticationService.getLoginName()).thenReturn(authUser);
        when(userRelationshipRepository.findPotentialFriendsWithNameLike(authUser, searchString))
                .thenReturn(List.of(foundUser));

        final List<IUserSearchResultVO> foundUsers = userSearchService.findUsersWithNamesContainingString(searchString);
        assertAll(() -> assertEquals(1, foundUsers.size(),
                        "List of found users should match the size of repository returned list!"),
                () -> assertEquals(searchResultVO.getLogin(), foundUsers.get(0).getLogin(),
                        "Login of returned user should match the login of repository returned DO!"),
                () -> assertEquals(searchResultVO.getName(), foundUsers.get(0).getName(),
                        "Name of returned user should match the name of repository returned DO!"),
                () -> assertEquals(searchResultVO.getRelationshipStatus() == null
                                ? null : searchResultVO.getRelationshipStatus().name(),
                        foundUsers.get(0).getRelationshipStatus() == null
                                ? null : foundUsers.get(0).getRelationshipStatus().name(),
                        "Relationship status should match the status of repository returned DO!"));
    }

    private IUserSearchResultVO prepareSearchResultVO(final IUserSearchResultDO foundUser) {
        return new IUserSearchResultVO() {
            @Override
            public String getLogin() {
                return foundUser.getLogin();
            }

            @Override
            public EnumRelationshipStatus getRelationshipStatus() {
                return null;
            }

            @Override
            public String getName() {
                return foundUser.getName();
            }

        };
    }

    private IUserSearchResultDO prepareSearchResultDO() {
        return new IUserSearchResultDO() {
            @Override
            public String getLogin() {
                return "null";
            }

            @Override
            public EnumRelationshipStatus getRelationshipStatus() {
                return null;
            }

            @Override
            public String getName() {
                return "null";
            }

        };
    }
}
