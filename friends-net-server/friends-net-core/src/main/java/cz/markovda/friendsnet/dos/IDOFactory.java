package cz.markovda.friendsnet.dos;


/**
 * @author <a href="mailto:">David Markov</a>
 * @since 25.12.21
 */
public interface IDOFactory {

    IUserDO createUser();
    IUserDO createUser(String login, String password);
    IUserDO createUser(int id, String login, String password);
}
