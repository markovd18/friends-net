package cz.markovda.friendsnet.auth.utils;

import cz.markovda.friendsnet.auth.dos.IUserDO;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
public class UserDOTestUtils {

    public static IUserDO prepareUser() {
        return prepareUser(0, "login", "password", IUserDO.EnumUserRole.USER);
    }

    public static IUserDO prepareUser(final String login) {
        return prepareUser(0, login, "password", IUserDO.EnumUserRole.USER);
    }

    public static IUserDO prepareEmptyUser() {
        return prepareUser(0, null, null, null);
    }

    public static IUserDO prepareUser(final int id, final String login,
                                      final String password, final IUserDO.EnumUserRole role) {
        return new IUserDO() {
            @Override
            public int getId() {
                return id;
            }

            @Override
            public String getLogin() {
                return login;
            }

            @Override
            public String getPassword() {
                return password;
            }

            @Override
            public String getName() {
                return "";
            }

            @Override
            public EnumUserRole getRole() {
                return role;
            }
        };
    }
}
