package cz.markovda.friendsnet.repository.impl;

import cz.markovda.friendsnet.dos.IUserDO;
import cz.markovda.friendsnet.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
@Repository
@RequiredArgsConstructor
public class UserRepository implements IUserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int saveUser(final IUserDO user) {
        return jdbcTemplate.update("INSERT INTO auth_user(login, \"password\") VALUES(?, ?)",  user.getLogin(), user.getPassword());
    }
}
