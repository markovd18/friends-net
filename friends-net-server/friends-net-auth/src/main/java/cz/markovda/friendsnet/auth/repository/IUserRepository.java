package cz.markovda.friendsnet.auth.repository;

import cz.markovda.friendsnet.auth.dos.IUserDO;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
public interface IUserRepository {

    @Transactional
    int saveUser(@NotNull @Valid IUserDO user);

    Optional<IUserDO> findUserWithRoleByLogin(@NotNull String login);

    boolean userWithLoginExists(@NotNull String login);
}
