package cz.markovda.friendsnet.service.impl;

import cz.markovda.friendsnet.service.IUserAuthService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 25.12.21
 */
public class UserAuthServiceImpl implements IUserAuthService {

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return User.builder()
                .username(username)
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();
    }
}
