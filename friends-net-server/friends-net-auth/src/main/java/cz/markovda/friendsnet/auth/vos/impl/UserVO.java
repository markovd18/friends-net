package cz.markovda.friendsnet.auth.vos.impl;

import cz.markovda.friendsnet.auth.vos.IUserVO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
public record UserVO(
        @NotNull @Length(min = 4, max = 50) @Email String login,
        @NotNull String password,
        @NotNull @Length(min = 3, max = 50) String name,
        Set<EnumUserRole> roles
) implements IUserVO {

    @Override
    public String getLogin() {
        return login();
    }

    @Override
    public String getPassword() {
        return password();
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public Set<EnumUserRole> getRoles() {
        return roles();
    }
}
