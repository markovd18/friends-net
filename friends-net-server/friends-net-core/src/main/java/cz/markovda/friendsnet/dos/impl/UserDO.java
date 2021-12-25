package cz.markovda.friendsnet.dos.impl;

import cz.markovda.friendsnet.dos.IUserDO;
import lombok.Builder;
import lombok.Data;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
@Data
@Builder
public class UserDO implements IUserDO {

    private int id;
    private String login;
    private String password;

    protected UserDO() {
    }

    protected UserDO(final int id, final String login, final String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    protected UserDO(final String login, final String password) {
        this.login = login;
        this.password = password;
    }
}
