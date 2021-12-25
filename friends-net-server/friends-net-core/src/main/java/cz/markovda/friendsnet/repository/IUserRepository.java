package cz.markovda.friendsnet.repository;

import cz.markovda.friendsnet.dos.IUserDO;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
@Repository
public interface IUserRepository {

    int saveUser(IUserDO user);

    Optional<IUserDO> findUserByLogin(String login);
}
