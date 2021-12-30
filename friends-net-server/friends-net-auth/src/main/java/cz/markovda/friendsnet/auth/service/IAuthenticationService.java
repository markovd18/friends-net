package cz.markovda.friendsnet.auth.service;

import cz.markovda.friendsnet.auth.vos.IUserVO;

import java.util.Collection;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public interface IAuthenticationService {

    String getLoginName();

    Collection<IUserVO.EnumUserRole> getRole();

    boolean isUserAnonymous();
}
