package cz.markovda.friendsnet.messaging.vos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 14.01.22
 */
public record InboundChatMessage(@NotNull @NotEmpty String to,
                                 @NotNull @NotEmpty String content) {
}
