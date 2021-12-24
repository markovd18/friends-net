package cz.markovda.friendsnet.dos.impl;

import cz.markovda.friendsnet.dos.IUserDO;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
public class DOFactory {

    public IUserDO createUser() {
        return new UserDO();
    }

    public IUserDO createUser(final String login, final String password) {
        return new UserDO(login, password);
    }
}
