package cz.markovda.friendsnet.messaging.service.impl;

import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import cz.markovda.friendsnet.messaging.service.IOnlineUsersCache;
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
public class OnlineUsersCache implements IOnlineUsersCache {

    public static final String FRIEND_STATUS_ENDPOINT = "/user/queue/friend-status";

    private final SimpMessagingTemplate simpleMessagingTemplate;
    private final IUserRelationshipRepository userRelationshipRepository;

    private final Map<String, String> onlineUserLogins = new HashMap<>();
    private final Object lock = new Object();

    @EventListener
    public void handleNewUserConnected(final SessionConnectedEvent event) {
        final String username = getUsernameFromSessionEvent(event);
        if (username != null) {
            doSynchronized(() -> distributeUserConnectedMessage(username));
        }
    }

    @EventListener
    public void handleUserDisconnected(final SessionDisconnectEvent event) {
        final String username = getUsernameFromSessionEvent(event);
        if (username != null) {
            doSynchronized(() -> distributeUserDisconnectedMessage(username));
        }
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

    private void distributeUserConnectedMessage(final String username) {
        distributeStatusChangeMessage(new FriendStatusChangedMessage(username, EnumFriendStatus.ONLINE));
        onlineUserLogins.put(username, null);
    }

    private void distributeUserDisconnectedMessage(final String username) {
        distributeStatusChangeMessage(new FriendStatusChangedMessage(username, EnumFriendStatus.OFFLINE));
        onlineUserLogins.remove(username);
    }

    private void distributeStatusChangeMessage(final FriendStatusChangedMessage message) {
        final String username = message.username();
        final List<String> onlineFriends = getOnlineFriends(message.username());
        if (message.status() == EnumFriendStatus.ONLINE) {
            sendOnlineFriendsInfoToUser(username, onlineFriends);
        }

        sendUserStatusChangeMessageToFriends(message, onlineFriends);
    }

    private void sendUserStatusChangeMessageToFriends(final FriendStatusChangedMessage message, final List<String> onlineFriends) {
        final List<FriendStatusChangedMessage> newUserOnlineMessageList = List.of(message);
        for (final String onlineFriend : onlineFriends) {
            simpleMessagingTemplate.convertAndSendToUser(onlineFriend, FRIEND_STATUS_ENDPOINT, newUserOnlineMessageList);
        }
    }

    private void sendOnlineFriendsInfoToUser(String username, List<String> onlineFriends) {
        final List<FriendStatusChangedMessage> onlineFriendsMessages = onlineFriends.stream()
                .map(login -> new FriendStatusChangedMessage(login, EnumFriendStatus.ONLINE))
                .collect(Collectors.toList());

        simpleMessagingTemplate.convertAndSendToUser(username, FRIEND_STATUS_ENDPOINT, onlineFriendsMessages);
    }

    private List<String> getOnlineFriends(final String username) {
        final Set<String> onlineUsers = onlineUserLogins.keySet();
        return userRelationshipRepository.findUsersFriendsUsernamesIn(username, onlineUsers);
    }
}
