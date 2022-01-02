package cz.markovda.friendsnet.friendship.dos.impl;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.IUserSearchResultDO;
import org.springframework.lang.Nullable;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 02.01.22
 */
public class UserSearchResultDO implements IUserSearchResultDO {

    private final String name;
    private final String login;
    private final EnumRelationshipStatus status;

    protected UserSearchResultDO(final String name, final String login,
                                 @Nullable final EnumRelationshipStatus status) {
        this.name = name;
        this.login = login;
        this.status = status;
    }

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
