package cz.markovda.friendsnet.repository.impl;

import cz.markovda.friendsnet.dos.IDOFactory;
import cz.markovda.friendsnet.dos.IUserDO;
import cz.markovda.friendsnet.dos.impl.DOFactory;
import cz.markovda.friendsnet.utils.UserDOTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        SimpleJdbcInsertOperations mock = mock(SimpleJdbcInsertOperations.class);
        userRepository.setUserInsertOperations(mock);
        DOFactory factory = new DOFactory();
        IUserDO user = factory.createUser("john.doe@email.com", "doe's password", "John Doe");
        prepareInsertOperationsMock(mock, user);

        when(jdbcTemplate.update(
                "INSERT INTO auth_user_role(id_user, id_role) VALUES (?, ?)", user.getLogin(), user.getRole().name())
        ).thenReturn(1);

        assertEquals(1, userRepository.saveUser(user), "Repository did not save user! (Affected line count was not 1)");
    }

    private void prepareInsertOperationsMock(SimpleJdbcInsertOperations mock, IUserDO user) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("login", user.getLogin());
        parameters.put("password", user.getPassword());
        parameters.put("name", user.getName());
        when(mock.executeAndReturnKey(parameters)).thenReturn(1);
    }

    @Test
    public void findsExistingUser() {

        var queryResult = new HashMap<String, Object>();
        queryResult.put("id", 1);
        queryResult.put("login", "login");
        queryResult.put("password", "password");
        queryResult.put("name", "Login Password");

        when(jdbcTemplate.queryForList(UserRepository.USER_WITH_ROLE_BY_LOGIN_QUERY, queryResult.get("login")))
                .thenReturn(List.of(queryResult));
        when(factory.createUser((int) queryResult.get("id"), (String) queryResult.get("login"),
                (String) queryResult.get("password"), (String) queryResult.get("name")))
                .thenReturn(UserDOTestUtils.prepareEmptyUser());

        final Optional<IUserDO> result = userRepository.findUserWithRoleByLogin((String) queryResult.get("login"));

        assertNotNull(result, "Query result may not be null!");
        assertTrue(result.isPresent(), "User repository should find an existing user!");
    }

    @Test
    public void findsExistingUser_withCorrectValues() {
        var queryResult = new HashMap<String, Object>();
        final int id = 1236;
        final String login = "some-login";
        final String password = "pass_word";
        final String name = "Someone With Credentials";

        queryResult.put("id", id);
        queryResult.put("login", login);
        queryResult.put("password", password);
        queryResult.put("name", name);

        when(jdbcTemplate.queryForList(UserRepository.USER_WITH_ROLE_BY_LOGIN_QUERY, queryResult.get("login")))
                .thenReturn(List.of(queryResult));
        when(factory.createUser(id, login, password, name)).thenReturn(UserDOTestUtils.prepareUser(id, login, password, IUserDO.EnumUserRole.USER));

        final Optional<IUserDO> result = userRepository.findUserWithRoleByLogin(login);
        assertNotNull(result, "Query result may not be null!");
        assertTrue(result.isPresent(), "User repository should find an existing user!");

        final IUserDO userDO = result.get();
        assertAll(() -> assertEquals(id, userDO.getId(), "ID of found user has to be equal to the DB record!"),
                () -> assertEquals(login, userDO.getLogin(), "Login of found user has to be equal to the DB record!"),
                () -> assertEquals(password, userDO.getPassword(), "Password of found user has to be equal to the DB record!"),
                () -> assertEquals(IUserDO.EnumUserRole.USER, userDO.getRole(), "Role of found user has to be equal to the DB record!"));
    }

    @Test
    public void throwsWhenSoughtLoginIsNull() {
        assertThrows(IllegalArgumentException.class, () -> userRepository.findUserWithRoleByLogin(null),
                "Searching for NULL login has to throw an exception!");
    }

    @Test
    public void throwsWhenSavedUserIsNull() {
        assertThrows(IllegalArgumentException.class, () -> userRepository.saveUser(null),
                "Saving NULL user has to throw an exception!");
    }
}