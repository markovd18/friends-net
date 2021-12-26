package cz.markovda.friendsnet.config;

import lombok.RequiredArgsConstructor;
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
public class DbConfiguration implements InitializingBean {

    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() {
        if (noDefaultRolesConfigured()) {
            insertDefaultRoles();
        }

        if (defaultAdminAccountDoesNotExist()) {
            insertDefaultAdminAccount();
        }
    }

    private void insertDefaultAdminAccount() {
        jdbcTemplate.update("INSERT INTO auth_user(id, login, password) VALUES (DEFAULT, ?, ?)", "admin", passwordEncoder.encode("strong-password"));
        final Integer idUser = jdbcTemplate.queryForObject("SELECT id FROM auth_user WHERE login = ?", Integer.class, "admin");
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
}
