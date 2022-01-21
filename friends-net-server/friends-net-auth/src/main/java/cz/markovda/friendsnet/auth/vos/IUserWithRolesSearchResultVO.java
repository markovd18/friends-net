package cz.markovda.friendsnet.auth.vos;

import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 21.01.22
 */
public interface IUserWithRolesSearchResultVO {

    String getLogin();

    String getName();

    Set<IUserVO.EnumUserRole> getRoles();
}
