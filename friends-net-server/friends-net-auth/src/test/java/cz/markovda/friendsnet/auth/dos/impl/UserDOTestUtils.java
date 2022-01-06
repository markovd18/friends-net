package cz.markovda.friendsnet.auth.dos.impl;

import cz.markovda.friendsnet.auth.dos.EnumUserRole;

import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
public class UserDOTestUtils {

    public static UserDO prepareUserDO() {
        return new UserDO(1, "login", "password");
    }

    public static UserDO prepareUserDO(final int id) {
        return new UserDO(id, "login", "password");
    }

    public static UserDO prepareUserDO(final String login) {
        return new UserDO(1, login, "password");
    }

    public static UserDO prepareUserDO(final int id, final String login, final String password, final Set<UserRoleDO> roles) {
        return new UserDO(id, login, password, roles);
    }

    public static UserRoleDO prepareRoleDO(EnumUserRole role) {
        return new UserRoleDO(role);
    }
}
