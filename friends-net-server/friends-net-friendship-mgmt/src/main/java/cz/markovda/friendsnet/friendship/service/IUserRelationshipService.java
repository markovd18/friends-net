package cz.markovda.friendsnet.friendship.service;

import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public interface IUserRelationshipService {

    @Transactional
    void createNewRelationship(@NotNull String receiverName);

    @Transactional
    void removeRelationship(@NotNull String otherLogin);

    @Transactional
    void acceptFriendRequest(@NotNull String senderLogin);

    @Transactional
    void blockUser(@NotNull String username);

    @Transactional
    void unblockUser(@NotNull String username);
}
