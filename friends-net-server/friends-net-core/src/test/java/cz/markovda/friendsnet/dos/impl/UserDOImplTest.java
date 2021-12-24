package cz.markovda.friendsnet.dos.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void emptyUserRetainsSetLogin() {
        emptyUser.setLogin("amazing login");
        assertNotNull(emptyUser.getLogin(), "User with login explicitly set should retain this value!");
    }

}
