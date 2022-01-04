package cz.markovda.friendsnet.messaging.controller;

import cz.markovda.friendsnet.friendship.service.IUserRelationshipService;
import cz.markovda.friendsnet.friendship.service.IUserSearchService;
import cz.markovda.friendsnet.messaging.vos.FriendStatusChangedMessage;
import cz.markovda.friendsnet.messaging.vos.StatusChangeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 04.01.22
 */
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate simpleMessagingTemplate;
    private final IUserSearchService userSearchService;

    @MessageMapping("/status-change")
    public void sendFriendStatusUpdateMessage(@Payload StatusChangeMessage message, Principal user) {
        final String username = user.getName();
        userSearchService.findUsersFriends(username)
                .forEach(searchResult -> simpleMessagingTemplate.convertAndSendToUser(
                        searchResult.getLogin(), "/user/queue/friend-status" , new FriendStatusChangedMessage(username, message.status())));
    }
}
