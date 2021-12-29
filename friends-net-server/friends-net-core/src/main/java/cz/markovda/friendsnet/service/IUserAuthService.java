package cz.markovda.friendsnet.service;

import cz.markovda.friendsnet.vos.IUserVO;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 25.12.21
 */
public interface IUserAuthService extends UserDetailsService {

    IUserVO findUserByUsername(@NotNull String username);

    IUserVO createNewUser(@NotNull @Valid IUserVO newUser);

}
