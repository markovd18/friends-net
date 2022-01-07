package cz.markovda.friendsnet.messaging.service;

import cz.markovda.friendsnet.messaging.vos.FriendStatusChangedMessage;

import java.util.List;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 06.01.22
 */
public interface IUserStatusDispatcher {

    List<FriendStatusChangedMessage> distributeOnlineFriendsStatusMessage(String username);
}
