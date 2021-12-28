package cz.markovda.friendsnet.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DbConfiguration implements InitializingBean {

    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public static final String ADMIN_LOGIN = "admin@admin.com";
    public static final String ADMIN_NAME = "Admin";
    public static final String ADMIN_PASSWORD = "Strong_password";

    @Override
    public void afterPropertiesSet() {
        if (noDefaultRolesConfigured()) {
            insertDefaultRoles();
        }

        if (defaultAdminAccountDoesNotExist()) {
            insertDefaultAdminAccount();
        }

        logAdminDetails();
    }

    private void insertDefaultAdminAccount() {
        jdbcTemplate.update("INSERT INTO auth_user(id, login, password, name) VALUES (DEFAULT, ?, ?, ?)",
                ADMIN_LOGIN, passwordEncoder.encode(ADMIN_PASSWORD), ADMIN_NAME);
        final Integer idUser = jdbcTemplate.queryForObject("SELECT id FROM auth_user WHERE login = ?", Integer.class, ADMIN_LOGIN);
        final Integer idRole = jdbcTemplate.queryForObject("SELECT id FROM auth_role WHERE name = ?", Integer.class, "ADMIN");
        jdbcTemplate.update("INSERT INTO auth_user_role(id_user, id_role) VALUES (?, ?)", idUser, idRole);
    }

    private boolean defaultAdminAccountDoesNotExist() {
        return jdbcTemplate.queryForList("SELECT (1) FROM auth_user WHERE login = ?", "admin").isEmpty();
    }

    private boolean noDefaultRolesConfigured() {
        return jdbcTemplate.queryForList("SELECT (1) FROM auth_role").isEmpty();
    }

    private void insertDefaultRoles() {
        jdbcTemplate.update("INSERT INTO auth_role(id, name) VALUES (DEFAULT, 'ADMIN')");
        jdbcTemplate.update("INSERT INTO auth_role(id, name) VALUES (DEFAULT, 'USER')");
    }

    private void logAdminDetails() {
        log.info("Default admin account available by logging in with login: {}, password: {}", ADMIN_LOGIN, ADMIN_PASSWORD);
    }
}
