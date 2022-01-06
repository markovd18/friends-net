package cz.markovda.friendsnet.messaging.controller;

import cz.markovda.friendsnet.messaging.vos.StatusChangeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 04.01.22
 */
@Controller
@RequiredArgsConstructor
public class WebSocketController {


    @MessageMapping("/status-change")
    public void sendFriendStatusUpdateMessage(@Payload StatusChangeMessage message, Principal user) {

    }
}
