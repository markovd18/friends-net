package cz.markovda.friendsnet.vos.impl;

import cz.markovda.friendsnet.vos.IUserVO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
public record UserVO(
        @NotNull @Length(min = 4, max = 50) @Pattern(regexp = "[a-zA-Z0-9_-]*") String login,
        @NotNull String password,
        EnumUserRole role
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
    public EnumUserRole getRole() {
        return role();
    }
}
