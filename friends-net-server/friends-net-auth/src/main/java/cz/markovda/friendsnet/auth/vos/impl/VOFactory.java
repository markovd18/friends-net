package cz.markovda.friendsnet.auth.vos.impl;

import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.friendsnet.auth.vos.IVOFactory;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 27.12.21
 */
@Component
public class VOFactory implements IVOFactory {

    @Override
    public IUserVO createUser(final String login, final String password, final String name) {
        return new UserVO(login, password, name, null);
    }
}
