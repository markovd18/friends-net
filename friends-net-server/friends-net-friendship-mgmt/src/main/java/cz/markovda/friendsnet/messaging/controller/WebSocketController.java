package cz.markovda.friendsnet.messaging.controller;

import cz.markovda.friendsnet.messaging.service.IUserStatusDispatcher;
import cz.markovda.friendsnet.messaging.vos.InboundChatMessage;
import cz.markovda.friendsnet.messaging.vos.OutboundChatMessage;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.Principal;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 04.01.22
 */
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate simpleMessagingTemplate;

    @MessageMapping("/chat")
    public void processChatMessage(@Payload @Valid @NotNull InboundChatMessage message, Principal user) {
        Assert.notNull(message, "Message may not be null!");
        simpleMessagingTemplate.convertAndSendToUser(message.to(), "/queue/chat",
                new OutboundChatMessage(user.getName(), message.content()));
    }
}
