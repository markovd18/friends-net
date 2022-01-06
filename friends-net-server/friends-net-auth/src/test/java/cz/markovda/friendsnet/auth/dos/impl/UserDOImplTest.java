package cz.markovda.friendsnet.auth.dos.impl;

import cz.markovda.friendsnet.auth.dos.EnumUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
public class UserDOImplTest {

    public static final String PASSWORD = "password";
    public static final String LOGIN = "login";

    private UserDO emptyUser;
    private UserDO userWithLoginAndPassword;

    @BeforeEach
    private void prepareTest() {
        emptyUser = new UserDO();
        userWithLoginAndPassword = new UserDO(LOGIN, PASSWORD);
    }

    @Test
    public void newUserHasEmptyLogin() {
        assertNull(emptyUser.getLogin(), "Newly created user DO should not have any login!");
    }

    @Test
    public void newUserHasEmptyPassword() {
        assertNull(emptyUser.getPassword(), "Newly created user DO should not have any password!");
    }

    @Test
    public void newUserHasLoginSet() {
        assertNotNull(userWithLoginAndPassword.getLogin(), "User with set login should retain it's login value!");
    }

    @Test
    public void newUserHasPasswordSet() {
        assertNotNull(userWithLoginAndPassword.getPassword(), "User with set password should retain it's password value!");
    }

    @Test
    public void newUserHasSpecificPasswordSet() {
        assertEquals(PASSWORD, userWithLoginAndPassword.getPassword(), "User with set password should retain it's password value!");
    }

    @Test
    public void newUserHasSpecificLoginSet() {
        assertEquals(LOGIN, userWithLoginAndPassword.getLogin(), "User with set login should retain it's login value!");
    }

    @Test
    public void userCreated_withIdLoginPasswordConstructor_hasAllAttributesSet() {
        final var id = 45;
        final var login = "extra good login";
        final var password = "hi mom";

        UserDO userDO = new UserDO(id, login, password);
        assertAll(() -> assertEquals(id, userDO.getId(), "Created user does not retain id set through constructor!"),
                () -> assertEquals(login, userDO.getLogin(), "Created user does not retain login set through constructor!"),
                () -> assertEquals(password, userDO.getPassword(), "Created user does not retain password set through constructor!"),
                () -> assertNotNull(userDO.getRoles(), "Set of user roles has to be empty, not null!"));
    }

    @Test
    public void userCreated_withIdLoginPasswordRoleConstructor_hasAllAttributesSet() {
        final var id = 1268;
        final var login = "";
        final var password = "encrypt me pls";
        final var roles = Set.of(new UserRoleDO(EnumUserRole.ADMIN));

        UserDO userDO = new UserDO(id, login, password, roles);
        assertAll(() -> assertEquals(id, userDO.getId(), "Created user does not retain id set through constructor!"),
                () -> assertEquals(login, userDO.getLogin(), "Created user does not retain login set through constructor!"),
                () -> assertEquals(password, userDO.getPassword(), "Created user does not retain password set through constructor!"),
                () -> assertIterableEquals(roles, userDO.getRoles(), "Created user does not retain role set through constructor!"));
    }


}
