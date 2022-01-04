package cz.markovda.friendsnet.messaging;

import org.springframework.context.event.EventListener;
import org.springframework.security.core.session.AbstractSessionEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 04.01.22
 */
@Component
public class OnlineUsersCache {

    private List<String> onlineUserLogins = new ArrayList<>();
    private final Object lock = new Object();

    @EventListener
    public void handleNewUserConnected(final SessionConnectedEvent event) {
        final String username = getUsernameFromSessionEvent(event);
        if (username != null) {
            doSynchronized(() -> onlineUserLogins.add(username));
        }
    }

    @EventListener
    public void handleUserDisconnected(final SessionDisconnectEvent event) {
        final String username = getUsernameFromSessionEvent(event);
        if (username != null) {
            doSynchronized(() -> onlineUserLogins.removeIf(login -> login.equals(username)));
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
}
