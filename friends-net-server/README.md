# Friends Net Server

Server side of the application consists of several Maven modules decomposing individual responsibilities into "independent" modules (not entirely since some modules are dependent on others, some are not).

## Modules

- **[Friends Net API](#friends-net-api)** - Open API generated server interface
- **[Start](#start)** - starting module, containing only starting Spring Boot class, depends on all other implemented modules
- **[Config](#config)** - configuration module, containing shared configuration between modules (eg. database connection)
- **[Authentication](#authentication)** - login, registration, authentication, user entities
- **[Friendship management](#friendship-management)** - user relationships and messaging
- **[Posts and announcements](#posts-and-announcements)**

### Friends net API

API module contains dependency on Open API generator plugin which generates entities and interfaces for other modules described in `api.yml` specification file located in `/api` directory in project root. All other modules that have any public interface have dependency on API module and may implement generated interfaces.

### Start

Start module is the starting module of the entire application. Other modules by themselves can be included in other applications and are independent of any web server bootstrapping logic. To start the application, class with `@SpringBootApplication` is present.

```Java
@SpringBootApplication
@EnableJpaRepositories
public class FriendsNetApplication {

    public static void main(String[] args) {
        SpringApplication.run(FriendsNetApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "client:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }
}
```

This main class bootstraps the application, enables scanning of entities (in `cz.markovda.friendsnet` packages) and JPA Repositories. Additionally, some other globally used beans are initialized.

Other modules are then required to implement their local module configuration class in `cz.markovda.friendsnet` package which will be scanned by `FriendsNetApplication` starting class. This makes sure that the module is "activated" in running application. They may extend component scan through other packages they implement their logic in. For example [Authentication module](#Authentication) implements this logic like this:

```Java
package cz.markovda.friendsnet;			// Configuration is visible to the starting class and can be scanned

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@ComponentScan(basePackages = "cz.markovda.friendsnet.auth")	// Extends component scan to local packages
public class FriendsNetAuthModule {

	@Bean
	public PasswordEncoder passwordEncoder() {		// Definition of module specific beans
		return new BCryptPasswordEncoder();
	}

}
```

### Config

Unfortunately, Spring maven modules have no way how to user `application.yml` configuration files of parent module, therefore shared configuration has to be located independently so modules may be dependent on it if need be. For this reason, Config module was created, containing only global module configuration. This is mainly used for local development purposes. Since this is just a prototype, production build also uses these configurations. For real production build, environment variables would be passed, eg. from `docker-compose.yml` stored in secret environment configuration file.

### Authentication

Authentication module contains all fundamental features required for user login, registration and authentication. Furthermore, web security configuration can be found in this module. Since it is the most low-level module of all, it is independent of all other business logic modules (only depends on [Config](#Config) and [API](#friends-net-api)). 

Web configuration is implemented in a way that allows anonymous users to login and register only. All other endpoints have limited access allowing connection to authenticated users only. The only exception is `/messaging/**` prefix, which is later used for web socket communication. Reason for it will be explained further in [messaging](#messaging) section.

```Java
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        jsr250Enabled = true,
        prePostEnabled = true,
        securedEnabled = true
)
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    ...

@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable()	// disabled for convenience - in real production has to be enabled
            	// anyone may send POST request on /login and /register
                .authorizeRequests().antMatchers(HttpMethod.POST, "/login", "/register").permitAll()
            	// messaging is available for web socket communication - uses authentication in own way
                .antMatchers("/messaging/**").permitAll()
            	// only admin operations are limited
                .antMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
                .and()
				// we are using JWT for authentication - therefore configuring custom entry point and stateless session management
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntrypoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // we need custom filter to parse the authorization header out of incomming requests
        http.addFilterAfter(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

As seen in code snippet above, JWT is used as a way of user authentication. It is a Bearer token containing user identification data (his username) and TTL. To make sure every request is authenticated, custom Filter is implemented which intercepts any incoming HTTP request and checks for Authorization header.

```Java
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final IUserAuthService userAuthService;
    private final JwtUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {
		// parsing Authorization header from request
        final String authorizationHeader = getAuthorizationHeader(request);
        // if present, we try to authenticate the user
        if (authorizationHeader != null) {
            authenticateFromAuthorizationHeader(request, authorizationHeader);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateFromAuthorizationHeader(HttpServletRequest request, String authorizationHeader) {
        if (jwtTokenUtils.tokenStartsWithBearerHeader(authorizationHeader)) {
            authenticateUserFromToken(request, authorizationHeader);
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }
    }

    private void authenticateUserFromToken(final HttpServletRequest request, final String requestTokenHeader) {
        JwtUtils.AuthenticationData authenticationData = parseAuthenticationData(requestTokenHeader);
        if (authenticationData != null && authenticationData.containsUsername() && isCurrentlyNobodyAuthenticated()) {
            setAuthentication(request, authenticationData);
        }
    }
    
    ...
        
}
```

If the Authentication header is present, filter checks if it contains valid Bearer JWT token and whether it is expired or not. If all checks pass, user is authenticated with his authorities for this request only. User may have multiple authorities. In this prototype they are limited to `USER` and `ADMIN`. Admin users are allowed to change authorities of their friends but not their own.

### Friendship management

Friendship management module is split into two parts - friendship management itself and [messaging](#messaging). In this section we will cover the friendship management part.

Each authenticated user may search for other registered users and send them friend request. Receiving user then may accept the request, decline it or block the sender entirely. If the request is accepted, users become friends and may exchange messages in real-time via [messaging](#messaging) submodule. 

When the receiver decides to simply decline the incoming friend request, sender is not notified and may send new friend request to the receiving user.

When receiver wishes not to receive any other friend request from specific sender, he may block him. Blocked user is then unable to send any further friend requests to the block initiator. If friendship is created between two users, one also may block the other and he will not be able to send him messages or see his on-line status. 

It is possible for the block initiator to unblock the end user. However, when user has been block by the other, after unblocking their relationship is entirely removed and new friend request has to be sent to create friendship again.

Searching is limited only on users which have pending friendship request from the searcher or have no relationship with him at all. This means that friends, users blocked by searcher or users who block the searcher will not be found by the search request.

### Messaging

Messaging sub-module allows friends to send each other real-time messages. This is made possible by web sockets implemented with STOMP protocol.

When logged in, users may connect via websocket to `/messaging` endpoint with STOMP protocol. Upon connection, server requires an Authorization STOPM header to come with the connection message. Inbound channel interceptor then parses JWT token from it the same way that the HTTP filter does and sets authentication for the entire web socket session. However, when connecting via STOMP, two requests are really sent:

- HTTP request to change protocol to WS
- WS connection message

Unfortunately native web sockets do not allow to send authentication headers when connecting. This is a problem since we use JWT authentication. To solve this issue,`/messaging/**` endpoints are available to all users. This is not a problem since behind this mapping only STOPM web socket services are located which means that all requests will still have to be authenticated but it will happen on the second incoming request - the WS protocol one.

```Java
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
                // if there is connection message incomming, we try to authenticate user
                // the same way as with HTTP requests
                if (isConnectMessage(accessor)) {
                    setWebSocketAuthentication(message, accessor);
                }
                return message;
            }
        });
    }



    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue");
        registry.setApplicationDestinationPrefixes("/messaging");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // we are tightly coupled with client implementation, running on localhost:3000
        // therefore we need to allow this origin to send requests to the server
        registry.addEndpoint("/messaging/chat")
                .setAllowedOriginPatterns("http://localhost:3000", "client:3000");
        registry.addEndpoint("/messaging/status-change")
                .setAllowedOriginPatterns("http://localhost:3000", "client:3000");
    }
    
    ...
        
}
```

### Posts and announcements

Registered users may create posts. These may contain anything that user may want to share with all his friends. Users may query the maximum of 20 latest posts. This post collection may be relative to current time or any other point in time.

Admin users are allowed to create special kind of posts - **Announcements**.

Announcements are visible to all users regardless of the relationship status between the author and viewing user. They obey the same rules as the regular posts do.

### Caveats

Database migration is handled by Flyway.
