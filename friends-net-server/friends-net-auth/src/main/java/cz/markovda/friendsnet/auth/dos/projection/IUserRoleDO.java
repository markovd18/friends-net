package cz.markovda.friendsnet.auth.dos.projection;

import cz.markovda.friendsnet.auth.dos.EnumUserRole;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 05.01.22
 */
public interface IUserRoleDO {

    Integer getId();

    EnumUserRole getName();
}
