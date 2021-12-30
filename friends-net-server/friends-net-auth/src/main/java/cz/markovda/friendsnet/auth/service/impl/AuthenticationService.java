package cz.markovda.friendsnet.auth.service.impl;

import cz.markovda.friendsnet.auth.service.IAuthenticationService;
import cz.markovda.friendsnet.auth.vos.IUserVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
@Service
public class AuthenticationService implements IAuthenticationService {

    private static final String ANONYMOUS_USER = "Anonymous";
    private static final String AUTHORITY_PREFIX = "ROLE_";

    @Override
    public String getLoginName() {
        if (isUserAnonymous()) {
            return ANONYMOUS_USER;
        }

        return getNameFromSecurityContext();
    }

    @Override
    public Collection<IUserVO.EnumUserRole> getRole() {
        if (isUserAnonymous()) {
            return Collections.emptyList();
        }

        return getRolesFromSecurityContext();
    }

    @Override
    public boolean isUserAnonymous() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    private String getNameFromSecurityContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Collection<IUserVO.EnumUserRole> getRolesFromSecurityContext() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(this::getRoleFromGrantedAuthority)
                .collect(Collectors.toList());
    }

    private IUserVO.EnumUserRole getRoleFromGrantedAuthority(final GrantedAuthority grantedAuthority) {
        return IUserVO.EnumUserRole.valueOf(grantedAuthority.getAuthority().substring(AUTHORITY_PREFIX.length()));
    }
}
