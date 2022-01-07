package cz.markovda.friendsnet.auth.config;

import cz.markovda.friendsnet.auth.dos.EnumUserRole;
import cz.markovda.friendsnet.auth.dos.impl.UserDO;
import cz.markovda.friendsnet.auth.dos.impl.UserRoleDO;
import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.repository.IUserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DbConfiguration implements InitializingBean {

    private final PasswordEncoder passwordEncoder;
    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;

    public static final String ADMIN_LOGIN = "admin@admin.com";
    public static final String ADMIN_NAME = "Admin";
    public static final String ADMIN_PASSWORD = "Strong_password";

    @Override
    public void afterPropertiesSet() {
        Set<UserRoleDO> defaultRoles = null;
        if (noDefaultRolesConfigured()) {
            defaultRoles = insertDefaultRoles();
        }

        if (defaultAdminAccountDoesNotExist()) {
            insertDefaultAdminAccount(defaultRoles);
        }

        logAdminDetails();
    }

    private void insertDefaultAdminAccount(Set<UserRoleDO> defaultRoles) {
        if (defaultRoles == null) {
            defaultRoles = getDefaultRoles();
        }

        final UserDO adminUser = new UserDO(ADMIN_LOGIN, passwordEncoder.encode(ADMIN_PASSWORD), ADMIN_NAME, defaultRoles);
        userRepository.save(adminUser);
    }

    private Set<UserRoleDO> getDefaultRoles() {
        return userRoleRepository.findAllByNameIn(Set.of(EnumUserRole.ADMIN, EnumUserRole.USER));
    }

    private boolean defaultAdminAccountDoesNotExist() {
        return !userRepository.existsByLogin(ADMIN_LOGIN);
    }

    private boolean noDefaultRolesConfigured() {
        return userRoleRepository.count() == 0;
    }

    private Set<UserRoleDO> insertDefaultRoles() {
        final UserRoleDO adminRole = new UserRoleDO(EnumUserRole.ADMIN);
        final UserRoleDO userRole = new UserRoleDO(EnumUserRole.USER);
        return new HashSet<>(userRoleRepository.saveAll(Set.of(adminRole, userRole)));
    }

    private void logAdminDetails() {
        log.info("Default admin account available by logging in with login: {}, password: {}", ADMIN_LOGIN, ADMIN_PASSWORD);
    }
}
