package cz.markovda.friendsnet.vos.impl;

import cz.markovda.friendsnet.vos.IUserSearchResultVO;
import org.springframework.lang.Nullable;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 02.01.22
 */
public record UserSearchResultVO(String name,
                                 String login,
                                 @Nullable EnumRelationshipStatus status) implements IUserSearchResultVO {

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Nullable
    @Override
    public EnumRelationshipStatus getRelationshipStatus() {
        return status;
    }
}
