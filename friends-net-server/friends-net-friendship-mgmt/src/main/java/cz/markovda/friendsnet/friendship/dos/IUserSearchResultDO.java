package cz.markovda.friendsnet.friendship.dos;

import org.springframework.lang.Nullable;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 02.01.22
 */
public interface IUserSearchResultDO {

    String getName();

    String getLogin();

    @Nullable
    EnumRelationshipStatus getRelationshipStatus();
}
