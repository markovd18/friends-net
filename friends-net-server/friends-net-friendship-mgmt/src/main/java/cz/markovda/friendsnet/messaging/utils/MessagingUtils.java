package cz.markovda.friendsnet.messaging.utils;

import org.springframework.messaging.Message;

import java.util.Map;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 13.01.22
 */
public class MessagingUtils {

    public static final String NATIVE_HEADERS = "nativeHeaders";

    public static Object getNativeHeader(final Message<?> message, final String headerName) {
        if (message == null || headerName == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        final Map<String, Object> nativeHeaders = (Map<String, Object>) message.getHeaders().get(NATIVE_HEADERS);
        if (nativeHeaders == null) {
            return null;
        }
        return nativeHeaders.get(headerName);
    }

    public static <T> T getHeader(final Message<?> message, final String headerName, final Class<T> type) {
        if (message == null || headerName == null) {
            return null;
        }

        return message.getHeaders().get(headerName, type);
    }
}
