package cz.markovda.friendsnet.messaging.controller;

import cz.markovda.friendsnet.messaging.service.IUserStatusDispatcher;
import cz.markovda.friendsnet.messaging.vos.FriendStatusChangedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 04.01.22
 */
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final IUserStatusDispatcher userStatusDispatcher;

    @SubscribeMapping("/user/queue/friend-status")
    public List<FriendStatusChangedMessage> subscribeToFriendStatuses(Principal user) {
        return userStatusDispatcher.distributeOnlineFriendsStatusMessage(user.getName());
    }
}
