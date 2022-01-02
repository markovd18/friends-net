package cz.markovda.friendsnet.auth.repository.impl;

import cz.markovda.friendsnet.auth.dos.IDOFactory;
import cz.markovda.friendsnet.auth.dos.IUserDO;
import cz.markovda.friendsnet.auth.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */
@RequiredArgsConstructor
@Repository
public class UserRepository implements IUserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final IDOFactory factory;

    private Map<String, Integer> userRolesNameToIDCache;
    @Setter
    private SimpleJdbcInsertOperations userInsertOperations;

    protected static final String USER_WITH_ROLE_BY_LOGIN_QUERY = "SELECT u.id, u.login, u.password, u.name, ar.name as role_name " +
            "FROM auth_user u " +
            "INNER JOIN auth_user_role aur ON u.id = aur.id_user " +
            "INNER JOIN auth_role ar ON ar.id = aur.id_role " +
            "WHERE login = ?";

    @PostConstruct
    public void init() {
        userInsertOperations = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("auth_user")
                .usingGeneratedKeyColumns("id");
    }

    @Transactional
    @Override
    public int saveUser(@NotNull @Valid final IUserDO user) {
        Assert.notNull(user, "Saved user may not be null!");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("login", user.getLogin());
        parameters.put("password", user.getPassword());
        parameters.put("name", user.getName());

        final int userId = userInsertOperations.executeAndReturnKey(parameters).intValue();

        if (user.getRole() != null) {
            jdbcTemplate.update("INSERT INTO auth_user_role(id_user, id_role) VALUES (?, ?)", userId, getUserRoleId(user.getRole()));
        }
        return userId;
    }

    @Override
    public Optional<IUserDO> findUserWithRoleByLogin(@NotNull final String login) {
        Assert.notNull(login, "Login must not be null!");
        final List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(USER_WITH_ROLE_BY_LOGIN_QUERY, login);
        if (queryResult.isEmpty()) {
            return Optional.empty();
        }

        final Map<String, Object> row = queryResult.get(0);
        return Optional.of(factory.createUser((Integer) row.get("id"),
                (String) row.get("login"), (String) row.get("password"),
                (String) row.get("name"), IUserDO.EnumUserRole.valueOf((String) row.get("role_name"))));
    }

    @Override
    public boolean userWithLoginExists(@NotNull final String login) {
        Assert.notNull(login, "Login must not be null!");
        final List<Integer> result = jdbcTemplate.queryForList("SELECT (1) FROM auth_user WHERE login = ?", Integer.class, login);
        return !result.isEmpty();
    }

    @Override
    public Optional<Integer> findUserId(@NotNull final String login) {
        Assert.notNull(login, "Login may not be null!");
        final List<Integer> ids = jdbcTemplate.queryForList("SELECT id FROM auth_user WHERE login = ?", Integer.class, login);
        return getSingleId(ids);
    }

    private void loadAllUserRoles() {
        final List<Map<String, Object>> roles = jdbcTemplate.queryForList("SELECT * FROM auth_role");
        userRolesNameToIDCache = new HashMap<>();
        for (var role : roles) {
            userRolesNameToIDCache.put((String) role.get("name"), (Integer) role.get("id"));
        }
    }

    private Integer getUserRoleId(final IUserDO.EnumUserRole userRole) {
        if (userRolesNotCached()) {
            loadAllUserRoles();
        }

        return userRolesNameToIDCache.get(userRole.name());
    }

    private boolean userRolesNotCached() {
        return userRolesNameToIDCache == null;
    }

    private Optional<Integer> getSingleId(final List<Integer> ids) {
        if (ids.isEmpty()) {
            return Optional.empty();
        }

        if (ids.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, ids.size());
        }

        return Optional.of(ids.get(0));
    }

    private List<IUserDO> createUserDOList(final List<Map<String, Object>> queryResult) {
        return queryResult.stream()
                .map(this::createUserDO)
                .collect(Collectors.toList());
    }

    private IUserDO createUserDO(final Map<String, Object> row) {
        return factory.createUser((Integer) row.getOrDefault("id", 0),
                (String) row.getOrDefault("login", null),
                (String) row.getOrDefault("password", null),
                (String) row.getOrDefault("name", null),
                IUserDO.EnumUserRole.valueOf((String) row.getOrDefault("role", "USER")));
    }
}
