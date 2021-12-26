package cz.markovda.friendsnet.repository;

import cz.markovda.friendsnet.dos.IUserDO;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
public interface IUserRepository {

    int saveUser(@NotNull IUserDO user);
    Optional<IUserDO> findUserWithRoleByLogin(@NotNull String login);
}
