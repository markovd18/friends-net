package cz.markovda.friendsnet.service.impl;

import cz.markovda.friendsnet.dos.IUserDO;
import cz.markovda.friendsnet.repository.IUserRepository;
import cz.markovda.friendsnet.service.IUserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 25.12.21
 */
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements IUserAuthService {

    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final IUserDO userDO = userRepository.findUserWithRoleByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with login " + username + " not found!"));

        return User.builder()
                .username(userDO.getLogin())
                .password(userDO.getPassword())
                .authorities("ROLE_" + userDO.getRole().name())
                .build();
    }
}
