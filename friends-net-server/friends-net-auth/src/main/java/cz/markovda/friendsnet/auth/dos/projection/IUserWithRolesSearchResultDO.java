package cz.markovda.friendsnet.auth.dos.projection;

import cz.markovda.friendsnet.auth.dos.EnumUserRole;

import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 21.01.22
 */
public interface IUserWithRolesSearchResultDO {

    String getLogin();

    String getName();

    Set<EnumUserRole> getRoles();
}
