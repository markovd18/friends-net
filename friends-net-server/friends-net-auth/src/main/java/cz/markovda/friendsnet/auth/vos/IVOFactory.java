package cz.markovda.friendsnet.auth.vos;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 27.12.21
 */
public interface IVOFactory {

    IUserVO createUser(String login, String password, String name);
}
