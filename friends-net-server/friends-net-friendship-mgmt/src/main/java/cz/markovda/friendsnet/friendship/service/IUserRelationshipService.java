package cz.markovda.friendsnet.friendship.service;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public interface IUserRelationshipService {

    void createNewRelationship(@NotNull String receiverName);
}
