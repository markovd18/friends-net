package cz.markovda.friendsnet.auth.dos.impl;

import cz.markovda.friendsnet.auth.dos.IUserDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
                () -> assertEquals(IUserDO.EnumUserRole.USER, userDO.getRole(), "Created user does not have default role USER!"));
    }

    @Test
    public void userCreated_withIdLoginPasswordRoleConstructor_hasAllAttributesSet() {
        final var id = 1268;
        final var login = "";
        final var password = "encrypt me pls";
        final var role = IUserDO.EnumUserRole.ADMIN;

        UserDO userDO = new UserDO(id, login, password, role);
        assertAll(() -> assertEquals(id, userDO.getId(), "Created user does not retain id set through constructor!"),
                () -> assertEquals(login, userDO.getLogin(), "Created user does not retain login set through constructor!"),
                () -> assertEquals(password, userDO.getPassword(), "Created user does not retain password set through constructor!"),
                () -> assertEquals(role, userDO.getRole(), "Created user does not retain role set through constructor!"));
    }

    @Test
    public void emptyUserRetainsSetLogin() {
        final var login = "amazing login";

        emptyUser.setLogin(login);
        assertEquals(login, emptyUser.getLogin(), "User with login explicitly set should retain this value!");
    }

    @Test
    public void userRetainsSetPassword() {
        final var password = "123456789";

        emptyUser.setPassword(password);
        assertEquals(password, emptyUser.getPassword(), "User DO does not retain set password!");
    }

    @Test
    public void userRetainsSetId() {
        final var id = 5;

        emptyUser.setId(id);
        assertEquals(id, emptyUser.getId(), "User DO does not retain set ID!");
    }

    @Test
    public void userRetainsSetRole() {
        final var role = IUserDO.EnumUserRole.USER;

        emptyUser.setRole(role);
        assertEquals(role, emptyUser.getRole(), "User DO does not retain set role!");
    }

}
