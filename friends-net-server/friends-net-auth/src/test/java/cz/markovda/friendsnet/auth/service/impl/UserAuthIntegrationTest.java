package cz.markovda.friendsnet.auth.service.impl;

import cz.markovda.friendsnet.auth.service.IUserAuthService;
import cz.markovda.friendsnet.auth.service.validation.impl.ValidationException;
import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.friendsnet.auth.vos.impl.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
@SpringBootTest
public class UserAuthIntegrationTest {

    @Autowired
    private IUserAuthService userAuthService;

    @Test
    public void canRegisterNewUser() {
        final IUserVO userVO = new UserVO("test@user.com", "test-password", "Test User", Set.of(IUserVO.EnumUserRole.USER));
        final IUserVO result = userAuthService.createNewUser(userVO);

        assertNotNull(result, "Created user should not be null!");
    }

    @Test
    public void registeringUserWithTooLongLoginFails() {
        final IUserVO userVO = new UserVO("very-long-login-that-is-very-much-likely-longer_than-required",
                "password", "Not relevant", Set.of(IUserVO.EnumUserRole.USER));

        assertThrows(ValidationException.class, () -> userAuthService.createNewUser(userVO),
                "Creating user with too long login has to throw an exception!");
    }

    @Test
    public void registeringUserWithTooShortLoginFails() {
        final IUserVO userVO = new UserVO("a", "password", "Some name", Set.of(IUserVO.EnumUserRole.USER));

        assertThrows(ValidationException.class, () -> userAuthService.createNewUser(userVO),
                "Creating user with too short login has to throw an exception!");
    }

    @Test
    public void registeringUserWithLogin_thatDoesNotMatchPatternFails() {
        final IUserVO userVO = new UserVO("login?with&badŮchars",
                "password", "Name with bad login", Set.of(IUserVO.EnumUserRole.USER));

        assertThrows(ValidationException.class, () -> userAuthService.createNewUser(userVO),
                "Creating user with invalid chars in login has to throw an exception!");
    }
}
