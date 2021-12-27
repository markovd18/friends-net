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
    private String name;
    private EnumUserRole role;

    protected UserDO() {
        this(null, null);
    }

    protected UserDO(final String login, final String password) {
        this(0, login, password);
    }

    protected UserDO(final int id, final String login, final String password) {
        this(id, login, password, EnumUserRole.USER);
    }

    protected UserDO(final int id, final String login, final String password, final EnumUserRole role) {
        this(id, login, password, null, role);
    }

    protected UserDO(final int id, final String login, final String password, final String name, final EnumUserRole role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.role = role;
    }
}
