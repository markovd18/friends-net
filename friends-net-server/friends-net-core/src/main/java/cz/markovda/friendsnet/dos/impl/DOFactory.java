package cz.markovda.friendsnet.dos.impl;

import cz.markovda.friendsnet.dos.IDOFactory;
import cz.markovda.friendsnet.dos.IUserDO;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
@Component
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

    @Override
    public IUserDO createUser(int id, String login, String password, IUserDO.EnumUserRole role) {
        return new UserDO(id, login, password, role);
    }
}
