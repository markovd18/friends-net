package cz.markovda.friendsnet.service.impl;

import cz.markovda.friendsnet.dos.IUserDO;
import cz.markovda.friendsnet.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 25.12.21
 */
public class UserAuthServiceTest {

    private UserAuthServiceImpl userAuthService;
    private IUserRepository userRepository;

    @BeforeEach
    public void prepareTest() {
        userRepository = mock(IUserRepository.class);
        userAuthService = new UserAuthServiceImpl(userRepository);
    }

    @Test
    public void loadsExistingUserByUsername() {
        final var username = "username";

        when(userRepository.findUserWithRoleByLogin(username)).thenReturn(prepareExistingUserResult());
        assertNotNull(userAuthService.loadUserByUsername("username"),
                "User loaded by user authentication service should not be null");
    }

    @Test
    public void throwsWhenUserDoesNotExist() {
        final var username = "not-existing-user";

        when(userRepository.findUserWithRoleByLogin(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userAuthService.loadUserByUsername(username),
                "Searching for non-existing user should throw an exception!");
    }

    private Optional<IUserDO> prepareExistingUserResult() {
        return Optional.of(new IUserDO() {
            @Override
            public int getId() {
                return 0;
            }

            @Override
            public String getLogin() {
                return "username";
            }

            @Override
            public String getPassword() {
                return "password";
            }

            @Override
            public EnumUserRole getRole() {
                return EnumUserRole.USER;
            }
        });
    }

}