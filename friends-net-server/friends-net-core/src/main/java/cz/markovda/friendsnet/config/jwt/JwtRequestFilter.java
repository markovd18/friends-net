package cz.markovda.friendsnet.config.jwt;

import cz.markovda.friendsnet.service.IUserAuthService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";

    private final IUserAuthService userAuthService;
    private final JwtUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {
        final String authorizationHeader = getAuthorizationHeader(request);
        if (authorizationHeader != null) {
            authenticateFromAuthorizationHeader(request, authorizationHeader);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateFromAuthorizationHeader(HttpServletRequest request, String authorizationHeader) {
        if (tokenStartsWithBearerHeader(authorizationHeader)) {
            authenticateUserFromToken(request, authorizationHeader);
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }
    }

    private void authenticateUserFromToken(final HttpServletRequest request, final String requestTokenHeader) {
        AuthenticationData authenticationData = parseAuthenticationData(requestTokenHeader);
        if (authenticationData != null && authenticationData.containsUsername() && isCurrentlyNobodyAuthenticated()) {
            setAuthentication(request, authenticationData);
        }
    }

    private boolean isCurrentlyNobodyAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private AuthenticationData parseAuthenticationData(final String requestTokenHeader) {
        final String jwtToken = requestTokenHeader.substring(BEARER_PREFIX.length());
        try {
            final String username = jwtTokenUtils.getUsernameFromToken(jwtToken);
            return new AuthenticationData(jwtToken, username);
        } catch (IllegalArgumentException e) {
            logger.error("Unable to get JWT Token");
        } catch (ExpiredJwtException e) {
            logger.warn("JWT Token has expired");
        }

        return null;
    }

    private boolean tokenStartsWithBearerHeader(final String requestTokenHeader) {
        return requestTokenHeader != null && requestTokenHeader.startsWith(BEARER_PREFIX);
    }

    private String getAuthorizationHeader(final HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    private void setAuthentication(final HttpServletRequest request, final AuthenticationData authenticationData) {
        final UserDetails userDetails = userAuthService.loadUserByUsername(authenticationData.username());
        if (jwtTokenUtils.isTokenValid(authenticationData.jwtToken(), userDetails)) {
            setAuthentication(request, userDetails);
        }
    }

    private void setAuthentication(final HttpServletRequest request, final UserDetails userDetails) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    record AuthenticationData(String username, String jwtToken) {

        boolean containsUsername() {
            return username != null;
        }
    }
}
