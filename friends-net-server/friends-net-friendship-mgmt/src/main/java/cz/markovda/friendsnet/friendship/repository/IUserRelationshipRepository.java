package cz.markovda.friendsnet.friendship.repository;

import cz.markovda.friendsnet.friendship.dos.IUserRelationshipDO;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public interface IUserRelationshipRepository {

    void saveNewRelationship(@NotNull IUserRelationshipDO relationshipDO);

    boolean relationshipExists(int senderId, int receiverId);
}
