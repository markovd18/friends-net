package cz.markovda.friendsnet.messaging.vos;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 04.01.22
 */
public record FriendStatusChangedMessage(
        String login,
        String name,
        EnumFriendStatus status) {
}
