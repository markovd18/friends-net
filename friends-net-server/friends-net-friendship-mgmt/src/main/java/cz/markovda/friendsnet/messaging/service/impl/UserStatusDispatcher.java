package cz.markovda.friendsnet.messaging.service.impl;

import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.friendship.dos.projection.IUserSearchResultDO;
import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import cz.markovda.friendsnet.messaging.service.IUserStatusDispatcher;
import cz.markovda.friendsnet.messaging.utils.MessagingUtils;
import cz.markovda.friendsnet.messaging.vos.EnumFriendStatus;
import cz.markovda.friendsnet.messaging.vos.FriendStatusChangedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 04.01.22
 */
@Component
@RequiredArgsConstructor
public class UserStatusDispatcher implements IUserStatusDispatcher {

    private final SimpMessagingTemplate simpleMessagingTemplate;
    private final IUserRelationshipRepository userRelationshipRepository;
    private final IUserRepository userRepository;

    private final Map<String, String> onlineUserLogins = new ConcurrentHashMap<>();

    @EventListener
    public void handleNewUserConnected(final SessionSubscribeEvent event) {
        if (isFriendStatusSubscriptionEvent(event)) {
            addNewOnlineUser(event);
        }
    }

    @EventListener
    public void handleUserDisconnected(final SessionDisconnectEvent event) {
        final String username = getUsernameFromSessionEvent(event);
        if (username != null) {
            distributeUserDisconnectedMessage(username);
        }
    }

    private void addNewOnlineUser(final SessionSubscribeEvent event) {
        final String username = getUsernameFromSessionEvent(event);
        if (username != null) {
            final String name = getNameByLogin(username);
            onlineUserLogins.put(username, name);
            distributeOnlineFriendsStatusMessage(username);
        }
    }

    private boolean isFriendStatusSubscriptionEvent(final SessionSubscribeEvent event) {
        final String destination = MessagingUtils.getHeader(event.getMessage(), "simpDestination", String.class);
        return destination != null && destination.equals("/user/queue/friend-status");
    }

    private String getNameByLogin(String username) {
        return userRepository.findNameByLogin(username)
                .orElseThrow(() -> new IllegalStateException("User's name not found by username " + username));
    }

    public void distributeOnlineFriendsStatusMessage(final String username) {
        final List<IUserSearchResultDO> onlineFriends = getOnlineFriends(username);
        final String name = onlineUserLogins.get(username);
        sendUserStatusChangeMessageToFriends(new FriendStatusChangedMessage(username, name, EnumFriendStatus.ONLINE), onlineFriends);
        final List<FriendStatusChangedMessage> messages = createFriendOnlineMessages(onlineFriends);
        sendUserStatusChangeMessagesToUsers(messages, List.of(username));
    }

    private List<FriendStatusChangedMessage> createFriendOnlineMessages(final List<IUserSearchResultDO> onlineFriends) {
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

    private void distributeUserDisconnectedMessage(final String username) {
        final String name = onlineUserLogins.getOrDefault(username, null);
        if (name != null) {
            distributeStatusChangeMessage(new FriendStatusChangedMessage(username, name, EnumFriendStatus.OFFLINE));
            onlineUserLogins.remove(username);
        }
    }

    private void distributeStatusChangeMessage(final FriendStatusChangedMessage message) {
        final List<IUserSearchResultDO> onlineFriends = getOnlineFriends(message.login());
        sendUserStatusChangeMessageToFriends(message, onlineFriends);
    }

    private void sendUserStatusChangeMessageToFriends(final FriendStatusChangedMessage message, final List<IUserSearchResultDO> onlineFriends) {
        final List<String> usernames = onlineFriends.stream()
                .map(IUserSearchResultDO::getLogin)
                .collect(Collectors.toList());
        sendUserStatusChangeMessagesToUsers(List.of(message), usernames);
    }

    private void sendUserStatusChangeMessagesToUsers(final List<FriendStatusChangedMessage> messages,
                                                     final List<String> usernames) {
        for (final String username : usernames) {
            simpleMessagingTemplate.convertAndSendToUser(username, "/queue/friend-status", messages);
        }
    }

    private List<IUserSearchResultDO> getOnlineFriends(final String username) {
        final Set<String> onlineUsers = onlineUserLogins.keySet();
        return userRelationshipRepository.findUsersFriendsByUsernameIn(username, onlineUsers);
    }
}
