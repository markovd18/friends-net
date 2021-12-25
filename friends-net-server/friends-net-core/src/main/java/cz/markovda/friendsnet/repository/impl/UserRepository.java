package cz.markovda.friendsnet.repository.impl;

import cz.markovda.friendsnet.dos.IDOFactory;
import cz.markovda.friendsnet.dos.IUserDO;
import cz.markovda.friendsnet.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
@RequiredArgsConstructor
public class UserRepository implements IUserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final IDOFactory factory;

    @Override
    public int saveUser(final IUserDO user) {
        return jdbcTemplate.update("INSERT INTO auth_user(login, \"password\") VALUES(?, ?)",  user.getLogin(), user.getPassword());
    }

    @Override
    public Optional<IUserDO> findUserByLogin(final String login) {
        Map<String, Object> queryResult = jdbcTemplate.queryForMap("SELECT * FROM auth_user WHERE login = ?", login);
        return Optional.of(factory.createUser((Integer) queryResult.get("id"),
                (String) queryResult.get("login"), (String) queryResult.get("password")));
    }
}
