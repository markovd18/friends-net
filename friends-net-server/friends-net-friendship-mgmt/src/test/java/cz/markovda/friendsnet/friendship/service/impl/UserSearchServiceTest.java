package cz.markovda.friendsnet.friendship.service.impl;

import cz.markovda.friendsnet.auth.dos.IUserDO;
import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.friendsnet.auth.vos.IVOFactory;
import cz.markovda.friendsnet.friendship.service.IUserSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public class UserSearchServiceTest {

    private IUserSearchService userSearchService;
    private IVOFactory authVoFactory;
    private IUserRepository userRepository;

    @BeforeEach
    public void prepareTest() {
        authVoFactory = mock(IVOFactory.class);
        userRepository = mock(IUserRepository.class);
        userSearchService = new UserSearchService(userRepository, authVoFactory);
    }

    @Test
    public void returnsNonNullList_whenNonNullStringPassed() {
        final var searchString = "search";

        when(userRepository.findUsersWithNameLike(searchString))
                .thenReturn(new ArrayList<>());

        assertNotNull(userSearchService.findUsersWithNamesContainingString(searchString),
                "User search should always return non null list!");
    }

    @Test
    public void returnsNonEmptyList_whenUsersFound() {
        final var searchString = "good search";
        final IUserDO foundUser = prepareUserDO();
        final IUserVO userVO = prepareUserVO(foundUser);

        when(userRepository.findUsersWithNameLike(searchString))
                .thenReturn(List.of(prepareUserDO()));
        when(authVoFactory.createUser(foundUser.getLogin(), null, foundUser.getName()))
                .thenReturn(userVO);

        final List<IUserVO> foundUsers = userSearchService.findUsersWithNamesContainingString(searchString);
        assertAll(() -> assertEquals(1, foundUsers.size(),
                        "List of found users should match the size of repository returned list!"),
                () -> assertEquals(userVO.getLogin(), foundUsers.get(0).getLogin(),
                        "Login of returned user should match the login of repository returned DO!"),
                () -> assertNull(foundUsers.get(0).getPassword(), "Service should not return user's password!"),
                () -> assertEquals(userVO.getName(), foundUsers.get(0).getName(),
                        "Name of returned user should match the name of repository returned DO!"));
    }

    private IUserVO prepareUserVO(final IUserDO user) {
        return new IUserVO() {
            @Override
            public String getLogin() {
                return user.getLogin();
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getName() {
                return user.getName();
            }

            @Override
            public EnumUserRole getRole() {
                return EnumUserRole.valueOf(user.getRole().name());
            }
        };
    }

    private IUserDO prepareUserDO() {
        return new IUserDO() {
            @Override
            public int getId() {
                return 0;
            }

            @Override
            public String getLogin() {
                return "null";
            }

            @Override
            public String getPassword() {
                return "null";
            }

            @Override
            public String getName() {
                return "null";
            }

            @Override
            public EnumUserRole getRole() {
                return EnumUserRole.USER;
            }
        };
    }
}
