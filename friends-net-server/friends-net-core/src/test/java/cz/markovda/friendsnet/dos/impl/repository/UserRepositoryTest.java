package cz.markovda.friendsnet.dos.impl.repository;

import cz.markovda.friendsnet.dos.IDOFactory;
import cz.markovda.friendsnet.dos.IUserDO;
import cz.markovda.friendsnet.dos.impl.DOFactory;
import cz.markovda.friendsnet.repository.impl.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
public class UserRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private IDOFactory factory;
    private UserRepository userRepository;

    @BeforeEach
    private void prepareTest() {
        jdbcTemplate = mock(JdbcTemplate.class);
        factory = mock(DOFactory.class);

        userRepository = new UserRepository(jdbcTemplate, factory);
    }

    @Test
    public void savesUser() {

        DOFactory factory = new DOFactory();
        IUserDO user = factory.createUser("john", "doe's password");
        when(jdbcTemplate.update(
                "INSERT INTO auth_user(login, \"password\") VALUES(?, ?)", user.getLogin(), user.getPassword())
        ).thenReturn(1);

        assertEquals(1, userRepository.saveUser(user), "Repository did not save user! (Affected line count was not 1)");
    }

    @Test
    public void findsExistingUser() {

        var queryResult = new HashMap<String, Object>();
        queryResult.put("id", 1);
        queryResult.put("login", "login");
        queryResult.put("password", "password");

        when(jdbcTemplate.queryForMap("SELECT * FROM auth_user WHERE login = ?", queryResult.get("login")))
                .thenReturn(queryResult);
        when(factory.createUser((int) queryResult.get("id"), (String) queryResult.get("login"), (String) queryResult.get("password")))
                .thenReturn(prepareEmptyUserDO());

        final Optional<IUserDO> result = userRepository.findUserByLogin((String) queryResult.get("login"));

        assertNotNull(result, "Query result may not be null!");
        assertTrue(result.isPresent(), "User repository should find an existing user!");
    }

    @Test
    public void findsExistingUser_withCorrectValues() {
        var queryResult = new HashMap<String, Object>();
        final int id = 1236;
        final String login = "some-login";
        final String password = "pass_word";

        queryResult.put("id", id);
        queryResult.put("login", login);
        queryResult.put("password", password);

        when(jdbcTemplate.queryForMap("SELECT * FROM auth_user WHERE login = ?", queryResult.get("login")))
                .thenReturn(queryResult);
        when(factory.createUser(id, login, password)).thenReturn(prepareUserDO(id, login, password));

        final Optional<IUserDO> result = userRepository.findUserByLogin(login);
        assertNotNull(result, "Query result may not be null!");
        assertTrue(result.isPresent(), "User repository should find an existing user!");

        final IUserDO userDO = result.get();
        assertAll(() -> assertEquals(id, userDO.getId(), "ID of found user has to be equal to the DB record!"),
                () -> assertEquals(login, userDO.getLogin(), "Login of found user has to be equal to the DB record!"),
                () -> assertEquals(password, userDO.getPassword(), "Password of found user has to be equal to the DB record!"));
    }

    private IUserDO prepareEmptyUserDO() {
        return new IUserDO() {
            @Override
            public int getId() {
                return 0;
            }

            @Override
            public String getLogin() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }
        };
    }

    private IUserDO prepareUserDO(final int id, final String login, final String password) {
        return new IUserDO() {
            @Override
            public int getId() {
                return id;
            }

            @Override
            public String getLogin() {
                return login;
            }

            @Override
            public String getPassword() {
                return password;
            }
        };
    }
}
