package cz.markovda.friendsnet.auth.vos.impl;

import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.friendsnet.auth.vos.IUserWithRolesSearchResultVO;

import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 21.01.22
 */
public record UserWithRolesSearchResultVO(
        String login,
        String name,
        Set<IUserVO.EnumUserRole> roles
) implements IUserWithRolesSearchResultVO {

    @Override
    public String getLogin() {
        return login();
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Set<IUserVO.EnumUserRole> getRoles() {
        return roles();
    }
}
