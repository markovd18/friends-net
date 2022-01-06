package cz.markovda.friendsnet.friendship.service;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 05.01.22
 */
public interface IRelationshipStatusService {

    Integer getRelationshipStatusId(EnumRelationshipStatus name);
}
