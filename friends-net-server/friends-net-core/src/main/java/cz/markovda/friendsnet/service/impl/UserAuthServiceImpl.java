package cz.markovda.friendsnet.service.impl;

import cz.markovda.friendsnet.dos.IDOFactory;
import cz.markovda.friendsnet.dos.IUserDO;
import cz.markovda.friendsnet.repository.IUserRepository;
import cz.markovda.friendsnet.service.IUserAuthService;
import cz.markovda.friendsnet.service.validation.Validator;
import cz.markovda.friendsnet.vos.IUserVO;
import cz.markovda.friendsnet.vos.impl.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 25.12.21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthServiceImpl implements IUserAuthService {

    private final IUserRepository userRepository;
    private final IDOFactory doFactory;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        log.debug("Start of loadUserByUsername method (args: {}).", username);

        final IUserDO userDO = userRepository.findUserWithRoleByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with login " + username + " not found!"));

        log.debug("End of loadUserByUsername method.");
        return User.builder()
                .username(userDO.getLogin())
                .password(userDO.getPassword())
                .authorities("ROLE_" + userDO.getRole().name())
                .build();
    }

    @Override
    public IUserVO createNewUser(@NotNull @Valid final IUserVO newUser) {
        log.debug("Start of createNewUser method (args: {}).", newUser);
        validator.validate(newUser);

        if (userRepository.userWithLoginExists(newUser.getLogin())) {
            throw new IllegalStateException("User already exists!");
        }

        final IUserVO savedUser = saveNewUser(newUser);

        log.debug("End of createNewUser method.");
        return savedUser;
    }

    private IUserVO saveNewUser(final IUserVO newUser) {
        log.debug("Saving new user...");
        final String encodedPassword = passwordEncoder.encode(newUser.getPassword());
        final IUserDO userDO = doFactory.createUser(newUser.getLogin(), encodedPassword);

        final boolean isNotSingleRowAffected = userRepository.saveUser(userDO) != 1;
        if (isNotSingleRowAffected) {
            throw new RuntimeException("Error while saving new user!");
        }

        log.debug("New user saved.");
        return new UserVO(userDO.getLogin(), userDO.getPassword(), IUserVO.EnumUserRole.valueOf(userDO.getRole().name()));
    }
}
