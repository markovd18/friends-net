package cz.markovda.friendsnet.repository.impl;

import cz.markovda.friendsnet.dos.IDOFactory;
import cz.markovda.friendsnet.dos.IUserDO;
import cz.markovda.friendsnet.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
@RequiredArgsConstructor
@Repository
public class UserRepository implements IUserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final IDOFactory factory;

    protected static final String USER_WITH_ROLE_BY_LOGIN_QUERY = "SELECT u.id, u.login, u.password, ar.name " +
            "FROM auth_user u " +
            "INNER JOIN auth_user_role aur ON u.id = aur.id_user " +
            "INNER JOIN auth_role ar ON ar.id = aur.id_role " +
            "WHERE login = ?";

    @Override
    public int saveUser(@NotNull final IUserDO user) {
        Assert.notNull(user, "Saved user may not be null!");
        return jdbcTemplate.update("INSERT INTO auth_user(login, \"password\") VALUES(?, ?)",  user.getLogin(), user.getPassword());
    }

    @Override
    public Optional<IUserDO> findUserWithRoleByLogin(@NotNull final String login) {
        Assert.notNull(login, "Login must not be null!");
        Map<String, Object> queryResult = jdbcTemplate.queryForMap(USER_WITH_ROLE_BY_LOGIN_QUERY, login);
        return Optional.of(factory.createUser((Integer) queryResult.get("id"),
                (String) queryResult.get("login"), (String) queryResult.get("password")));
    }
}
