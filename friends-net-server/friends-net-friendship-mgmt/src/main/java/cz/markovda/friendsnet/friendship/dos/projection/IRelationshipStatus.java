package cz.markovda.friendsnet.friendship.dos.projection;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 05.01.22
 */
public interface IRelationshipStatus {

    Integer getId();

    EnumRelationshipStatus getName();
}
