package cz.markovda.friendsnet.dos.impl.repository;

import cz.markovda.friendsnet.dos.IUserDO;
import cz.markovda.friendsnet.dos.impl.DOFactory;
import cz.markovda.friendsnet.repository.impl.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
public class UserRepositoryTest {

    @BeforeEach
    private void prepareTest() {

    }

    @Test
    public void savesUser() {

        DOFactory factory = new DOFactory();
        IUserDO user = factory.createUser("john", "doe's password");
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.update(
                "INSERT INTO auth_user(login, \"password\") VALUES(?, ?)", user.getLogin(), user.getPassword())
        ).thenReturn(1);

        UserRepository userRepository = new UserRepository(jdbcTemplate);

        assertEquals(1, userRepository.saveUser(user), "Repository did not save user! (Affected line count was not 1)");
    }
}
