package cz.markovda.friendsnet.dos.impl;

import cz.markovda.friendsnet.dos.IUserDO;
import lombok.Data;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
@Data
public class UserDO implements IUserDO {

    public String login;
    public String password;

    protected UserDO() {
    }

    protected UserDO(final String login, final String password) {
        this.login = login;
        this.password = password;
    }
}
