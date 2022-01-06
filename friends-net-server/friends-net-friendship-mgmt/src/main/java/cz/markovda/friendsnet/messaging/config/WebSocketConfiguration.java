package cz.markovda.friendsnet.messaging.config;

import cz.markovda.friendsnet.auth.config.jwt.JwtUtils;
import cz.markovda.friendsnet.auth.service.IUserAuthService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 04.01.22
 */
@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final JwtUtils jwtUtils;
    private final IUserAuthService userAuthService;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (isConnectMessage(accessor)) {
                    setWebSocketAuthentication(message, accessor);
                }
                return message;
            }
        });
    }



    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/user/queue/specific-user", "/user/queue/friend-status");
        registry.setApplicationDestinationPrefixes("/messaging");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/messaging/chat")
                .setAllowedOriginPatterns("http://localhost:3000", "client:3000");
        registry.addEndpoint("/messaging/status-change")
                .setAllowedOriginPatterns("http://localhost:3000", "client:3000");
    }

    private void setWebSocketAuthentication(Message<?> message, StompHeaderAccessor accessor) {
        final String authorizationHeader = getAuthorizationHeader(message);
        if (jwtUtils.tokenStartsWithBearerHeader(authorizationHeader)) {
            setJwtWebSocketAuthentication(accessor, authorizationHeader);
        }
    }

    private void setJwtWebSocketAuthentication(StompHeaderAccessor accessor, String authorizationHeader) {
        JwtUtils.AuthenticationData authenticationData = parseAuthenticationData(authorizationHeader);
        if (authenticationData != null && authenticationData.containsUsername()) {
            setUserAuthentication(accessor, authenticationData);
        }
    }

    private void setUserAuthentication(StompHeaderAccessor accessor, JwtUtils.AuthenticationData authenticationData) {
        final UserDetails userDetails = userAuthService.loadUserByUsername(authenticationData.username());
        if (jwtUtils.isTokenValid(authenticationData.jwtToken(), userDetails)) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            accessor.setUser(usernamePasswordAuthenticationToken);
        }
    }

    private String getAuthorizationHeader(final Message<?> message) {
        final MessageHeaders headers = message.getHeaders();
        @SuppressWarnings("unchecked")
        Map<String, Object> nativeHeaders = (Map<String, Object>) headers.get("nativeHeaders");
        @SuppressWarnings("unchecked")
        final List<String> values = (List<String>) nativeHeaders.get("Authorization");
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    private JwtUtils.AuthenticationData parseAuthenticationData(final String requestTokenHeader) {
        try {
            return jwtUtils.parseAuthenticationData(requestTokenHeader);
        } catch (IllegalArgumentException | ExpiredJwtException e) {
            return null;
        }
    }

    private boolean isConnectMessage(final StompHeaderAccessor accessor) {
        return accessor != null && accessor.getCommand() == StompCommand.CONNECT;
    }
}
