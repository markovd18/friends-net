package cz.markovda.friendsnet.dos.impl;

import cz.markovda.friendsnet.dos.IUserDO;
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
public class DOFactoryTest {

    private DOFactory factory;

    @BeforeEach
    private void prepareTest() {
        factory = new DOFactory();
    }

    @Test
    public void createsEmptyUserDO() {
        assertNotNull(factory.createUser(), "Factory created instance of user DO should not be null!");
    }

    @Test
    public void createsEmptyUser_withCorrectValues() {
        IUserDO userDO = factory.createUser();
        assertAll(() -> assertEquals(0, userDO.getId(), "ID of empty user DO should be 0!"),
                () -> assertNull(userDO.getLogin(), "Login of empty user DO should be null!"),
                () -> assertNull(userDO.getPassword(), "Password of empty user DO should be null!"));
    }

    @Test
    public void createsUserWithLogin_andPassword() {
        assertNotNull(factory.createUser("login", "password"), "Factory created instance od DO should not be null!");
    }

    @Test
    public void createsUserWithLogin_andPassword_andCorrectValues() {
        final String login = "crazy-login";
        final String password = "Super_Strong_Password_123";

        IUserDO userDO = factory.createUser(login, password);
        assertAll(() -> assertEquals(0, userDO.getId(), "Not set ID of factory created user DO should be 0!"),
                () -> assertEquals(login, userDO.getLogin(), "Factory created user DO's login should match the factory argument!"),
                () -> assertEquals(password, userDO.getPassword(), "Factory created user DO's password should match the factory argument!"));
    }

    @Test
    public void createsUserWithAllAttributes() {
        assertNotNull(factory.createUser(1, "john_doe", "string-password"));
    }

    @Test
    public void createsUserWithAllAttributes_andWithCorrectValues() {
        final int id = 15;
        final String login = "test-login";
        final String password = "weak";

        IUserDO userDO = factory.createUser(id, login, password);
        assertAll(() -> assertEquals(id, userDO.getId(), "Factory created user DO's ID should match the factory argument!"),
                () -> assertEquals(login, userDO.getLogin(), "Factory created user DO's login should match the factory argument!"),
                () -> assertEquals(password, userDO.getPassword(), "Factory created user DO's password should match the factory argument!"));

    }
}
