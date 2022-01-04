package cz.markovda.friendsnet.friendship.vos;

import org.springframework.lang.Nullable;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 02.01.22
 */
public interface IUserSearchResultVO {

    String getName();

    String getLogin();

    @Nullable
    EnumRelationshipStatus getRelationshipStatus();

    enum EnumRelationshipStatus {
        REQUEST_SENT,
        FRIENDS,
        BLOCKED
    }
}
