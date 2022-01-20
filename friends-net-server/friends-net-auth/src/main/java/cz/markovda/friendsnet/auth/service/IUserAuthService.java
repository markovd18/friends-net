package cz.markovda.friendsnet.auth.service;

import cz.markovda.friendsnet.auth.service.role.EnumUserRoleAction;
import cz.markovda.friendsnet.auth.vos.IUserVO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 25.12.21
 */
public interface IUserAuthService extends UserDetailsService {

    IUserVO findUserByUsername(@NotNull String username);

    @Transactional
    IUserVO createNewUser(@NotNull @Valid IUserVO newUser);

    @Transactional
    void changeUserRole(@NotNull String username, @NotNull IUserVO.EnumUserRole role, @NotNull EnumUserRoleAction action);
}
