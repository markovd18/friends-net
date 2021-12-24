package cz.markovda.friendsnet.repository;

import cz.markovda.friendsnet.dos.IUserDO;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
public interface IUserRepository {

    int saveUser(IUserDO user);
}
