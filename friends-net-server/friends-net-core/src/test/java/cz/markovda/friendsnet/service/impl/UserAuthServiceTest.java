package cz.markovda.friendsnet.service.impl;

import cz.markovda.friendsnet.repository.IUserRepository;
import cz.markovda.friendsnet.repository.impl.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 25.12.21
 */
public class UserAuthServiceTest {

    @Test
    public void loadsExistingUserByUsername() {
        IUserRepository userRepository = mock(UserRepository.class);
        UserAuthServiceImpl userAuthService = new UserAuthServiceImpl();
        assertNotNull(userAuthService.loadUserByUsername("username"),
                "User loaded by user authentication service should not be null");
    }


}
