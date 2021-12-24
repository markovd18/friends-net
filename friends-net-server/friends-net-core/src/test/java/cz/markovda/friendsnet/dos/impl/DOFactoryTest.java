package cz.markovda.friendsnet.dos.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    public void createsUserWithLogin_andPassword() {
        assertNotNull(factory.createUser("login", "password"), "Factory created instance od DO should not be null!");
    }
}
