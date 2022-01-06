package cz.markovda.friendsnet.auth.vos;


import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
public interface IUserVO {

    String getLogin();

    String getPassword();

    String getName();

    Set<EnumUserRole> getRoles();

    enum EnumUserRole {
        ADMIN,
        USER
    }
}
