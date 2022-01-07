package cz.markovda.friendsnet.messaging.service.impl;

import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.friendship.dos.projection.IUserSearchResultDO;
import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import cz.markovda.friendsnet.messaging.service.IUserStatusDispatcher;
import cz.markovda.friendsnet.messaging.vos.EnumFriendStatus;
import cz.markovda.friendsnet.messaging.vos.FriendStatusChangedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 04.01.22
 */
@Component
@RequiredArgsConstructor
public class UserStatusDispatcher implements IUserStatusDispatcher {

    public static final String FRIEND_STATUS_ENDPOINT = "/queue/friend-status";

    private final SimpMessagingTemplate simpleMessagingTemplate;
    private final IUserRelationshipRepository userRelationshipRepository;
    private final IUserRepository userRepository;

    private final Map<String, String> onlineUserLogins = new HashMap<>();
    private final Object lock = new Object();

    @EventListener
    public void handleNewUserConnected(final SessionConnectedEvent event) {
        final String username = getUsernameFromSessionEvent(event);
        if (username != null) {
            final String name = getNameByLogin(username);
            doSynchronized(() -> onlineUserLogins.put(username, name));
        }
    }

    private String getNameByLogin(String username) {
        return userRepository.findNameByLogin(username)
                .orElseThrow(() -> new IllegalStateException("User's name not found by username " + username));
    }

    @EventListener
    public void handleUserDisconnected(final SessionDisconnectEvent event) {
        final String username = getUsernameFromSessionEvent(event);
        if (username != null) {
            doSynchronized(() -> distributeUserDisconnectedMessage(username));
        }
    }

    @Override
    public synchronized List<FriendStatusChangedMessage> distributeOnlineFriendsStatusMessage(final String username) {
        final List<IUserSearchResultDO> onlineFriends = getOnlineFriends(username);
        final String name = onlineUserLogins.get(username);
        sendUserStatusChangeMessageToFriends(new FriendStatusChangedMessage(username, name, EnumFriendStatus.ONLINE), onlineFriends);
        return createFriendOnlineMessages(onlineFriends);
    }

    private List<FriendStatusChangedMessage> createFriendOnlineMessages(List<IUserSearchResultDO> onlineFriends) {
        return onlineFriends.stream()
                .map(user -> new FriendStatusChangedMessage(user.getLogin(), user.getName(), EnumFriendStatus.ONLINE))
                .collect(Collectors.toList());
    }

    private String getUsernameFromSessionEvent(final AbstractSubProtocolEvent event) {
        final Principal userData = event.getUser();
        if (userData == null) {
            return null;
        }

        return userData.getName();
    }

    private void doSynchronized(final Runnable runnable) {
        synchronized (lock) {
            runnable.run();
        }
    }

    private void distributeUserDisconnectedMessage(final String username) {
        final String name = onlineUserLogins.get(username);
        distributeStatusChangeMessage(new FriendStatusChangedMessage(username, name, EnumFriendStatus.OFFLINE));
        onlineUserLogins.remove(username);
    }

    private void distributeStatusChangeMessage(final FriendStatusChangedMessage message) {
        final List<IUserSearchResultDO> onlineFriends = getOnlineFriends(message.username());
        sendUserStatusChangeMessageToFriends(message, onlineFriends);
    }

    private void sendUserStatusChangeMessageToFriends(final FriendStatusChangedMessage message, final List<IUserSearchResultDO> onlineFriends) {
        final List<FriendStatusChangedMessage> newUserOnlineMessageList = List.of(message);
        for (final var onlineFriend : onlineFriends) {
            simpleMessagingTemplate.convertAndSend("/user/" + onlineFriend.getLogin() +  FRIEND_STATUS_ENDPOINT, newUserOnlineMessageList);
        }
    }

    private List<IUserSearchResultDO> getOnlineFriends(final String username) {
        final Set<String> onlineUsers = onlineUserLogins.keySet();
        return userRelationshipRepository.findUsersFriendsByUsernameIn(username, onlineUsers);
    }
}
