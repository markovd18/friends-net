package cz.markovda.friendsnet.auth.service.impl;

import cz.markovda.friendsnet.auth.dos.EnumUserRole;
import cz.markovda.friendsnet.auth.dos.IUserRoleDO;
import cz.markovda.friendsnet.auth.dos.impl.UserDO;
import cz.markovda.friendsnet.auth.dos.impl.UserRoleDO;
import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.repository.IUserRoleRepository;
import cz.markovda.friendsnet.auth.service.IUserAuthService;
import cz.markovda.friendsnet.auth.service.validation.IValidator;
import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.friendsnet.auth.vos.impl.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 25.12.21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthServiceImpl implements IUserAuthService {

    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IValidator validator;

    private Map<EnumUserRole, Integer> userRoleToIdCache;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        log.debug("Start of loadUserByUsername method (args: {}).", username);

        final UserDO userDO = userRepository.findByLoginFetchRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with login " + username + " not found!"));

        log.debug("End of loadUserByUsername method.");
        return User.builder()
                .username(userDO.getLogin())
                .password(userDO.getPassword())
                .authorities(userDO.getRoles().stream()
                        .map(IUserRoleDO::getName)
                        .map(Enum::name)
                        .map(roleName -> "ROLE_" + roleName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public IUserVO findUserByUsername(final String username) {
        log.debug("Start of findUserByUsername method (args: {}).", username);

        final UserDO userDO = userRepository.findByLoginFetchRoles(username)
                .orElse(null);
        if (userDO == null) {
            return null;
        }

        log.debug("End of findUserByUsername method.");
        return new UserVO(userDO.getLogin(), null, userDO.getName(), createUserRolesVO(userDO.getRoles()));
    }

    @Override
    public IUserVO createNewUser(@NotNull @Valid final IUserVO newUser) {
        log.debug("Start of createNewUser method (args: {}).", newUser);
        validator.validate(newUser);

        if (userRepository.existsByLogin(newUser.getLogin())) {
            throw new IllegalStateException("User already exists!");
        }

        final IUserVO savedUser = saveNewUser(newUser);

        log.debug("End of createNewUser method.");
        return savedUser;
    }

    private IUserVO saveNewUser(final IUserVO newUser) {
        log.debug("Saving new user...");
        final UserDO userDO = createNewUserDO(newUser);
        final UserDO savedUser = userRepository.save(userDO);
        if (savedUser.getId() == 0) {
            throw new RuntimeException("Error while saving new user!");
        }

        log.debug("New user saved.");
        return new UserVO(userDO.getLogin(), null, userDO.getName(), createUserRolesVO(userDO.getRoles()));
    }

    private UserDO createNewUserDO(final IUserVO newUser) {
        final String encodedPassword = passwordEncoder.encode(newUser.getPassword());
        final UserDO userDO = new UserDO(newUser.getLogin(), encodedPassword, newUser.getName());
        userDO.addRole(getUserRoleDO(EnumUserRole.USER));
        return userDO;
    }

    private Set<IUserVO.EnumUserRole> createUserRolesVO(final Set<UserRoleDO> userRolesDO) {
        return userRolesDO.stream()
                .map(IUserRoleDO::getName)
                .map(Enum::name)
                .map(IUserVO.EnumUserRole::valueOf)
                .collect(Collectors.toSet());
    }

    private UserRoleDO getUserRoleDO(final EnumUserRole roleName) {
        if (areUserRoleIdsNotCached()) {
            cacheUserRoles();
        }

        final Integer roleId = userRoleToIdCache.get(roleName);
        return userRoleRepository.getById(roleId);
    }

    private boolean areUserRoleIdsNotCached() {
        return userRoleToIdCache == null;
    }

    private void cacheUserRoles() {
        this.userRoleToIdCache = new HashMap<>();

        final Set<IUserRoleDO> roles = userRoleRepository.findAllReadOnly();
        for (final IUserRoleDO role : roles) {
            cacheUserRole(role);
        }
    }

    private void cacheUserRole(final IUserRoleDO role) {
        userRoleToIdCache.put(role.getName(), role.getId());
    }
}
