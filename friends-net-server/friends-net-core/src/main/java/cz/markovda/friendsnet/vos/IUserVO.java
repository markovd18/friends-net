package cz.markovda.friendsnet.vos;


/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
public interface IUserVO {

    String getLogin();

    String getPassword();

    EnumUserRole getRole();

    enum EnumUserRole {
        ADMIN,
        USER
    }
}
