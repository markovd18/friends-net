package cz.markovda.friendsnet.dos.impl;

import cz.markovda.friendsnet.dos.IDOFactory;
import cz.markovda.friendsnet.dos.IUserDO;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
public class DOFactory implements IDOFactory {

    @Override
    public IUserDO createUser() {
        return new UserDO();
    }

    @Override
    public IUserDO createUser(final String login, final String password) {
        return new UserDO(login, password);
    }

    @Override
    public IUserDO createUser(final int id, final String login, final String password) {
        return new UserDO(id, login, password);
    }
}
