package cz.markovda.friendsnet.friendship.repository.impl;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.IDOFactory;
import cz.markovda.friendsnet.friendship.dos.IUserRelationshipDO;
import cz.markovda.friendsnet.friendship.dos.IUserSearchResultDO;
import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
@Repository
@RequiredArgsConstructor
public class UserRelationshipRepository implements IUserRelationshipRepository {

    private final JdbcTemplate jdbcTemplate;
    private final IDOFactory factory;

    private Map<EnumRelationshipStatus, Integer> relationshipStatusToIdCache;

    protected static final String INSERT_NEW_RELATIONSHIP_QUERY = "INSERT INTO user_relationship(" +
            "id_sender, id_receiver, id_status, created_at, last_updated) VALUES (?, ?, ?, ?, ?)";

    protected static final String FIND_POTENTIAL_FRIENDS_QUERY =
            "SELECT au.name, au.login, rs.name AS status FROM auth_user au " +
            "left join user_relationship ur ON au.id IN (ur.id_sender, ur.id_receiver) AND (" +
                    "SELECT id FROM auth_user WHERE auth_user.login = ?) IN (ur.id_sender, ur.id_receiver) " +
            "LEFT JOIN relationship_status rs ON rs.id = ur.id_status " +
            "WHERE lower(au.name) LIKE ? AND (rs IS NULL OR rs.name = 'REQUEST_SENT') AND au.login != ?";

    protected static final String FIND_USERS_WITH_RELATIONSHIP_TO_USER =
            "SELECT au.login, au.name, rs.name as status FROM auth_user au " +
                "INNER JOIN user_relationship ur ON au.id IN (ur.id_sender, ur.id_receiver) AND (" +
                    "SELECT id FROM auth_user WHERE auth_user.login = ?) IN (ur.id_sender, ur.id_receiver) " +
                "INNER JOIN relationship_status rs ON ur.id_status = rs.id " +
            "where rs.id = ? and au.login != ?";

    protected static final String FIND_SENDERS_OF_REQUEST_TO_USER_WITH_STATUS =
            "SELECT au2.name, au2.login FROM ("+
                "SELECT ur.id_sender FROM auth_user au " +
                    "INNER JOIN user_relationship ur ON au.id = ur.id_receiver " +
                    "INNER JOIN relationship_status rs ON rs.id = ur.id_status " +
                "WHERE rs.id = ? and au.login = ?) AS i " +
            "INNER JOIN auth_user au2 ON i.id_sender = au2.id";

    protected static final String REMOVE_RELATIONSHIP_QUERY =
            "DELETE FROM user_relationship " +
            "WHERE (id_sender, id_receiver) IN ( " +
                "SELECT ur2.id_sender, ur2.id_receiver FROM (" +
                    "SELECT ur.id_sender, ur.id_receiver FROM user_relationship ur " +
                        "INNER JOIN auth_user au on au.id = ur.id_receiver or au.id = ur.id_sender " +
                    "WHERE au.login = ?) as ur2 " +
                    "INNER JOIN auth_user au2 on ur2.id_sender = au2.id or ur2.id_receiver = au2.id WHERE au2.login = ?)";

    protected static final String FIND_USER_RELATIONSHIP =
            "SELECT ur.*, rs.name AS status FROM user_relationship ur " +
                    "INNER JOIN relationship_status rs ON ur.id_status = rs.id " +
                    "INNER JOIN auth_user au ON au.id IN (ur.id_sender, ur.id_receiver) AND (" +
                        "SELECT id FROM auth_user WHERE auth_user.login = ?" +
                    ") IN (ur.id_sender, ur.id_receiver) " +
            "where au.login like ?";

    @Override
    public void saveNewRelationship(@NotNull final IUserRelationshipDO relationshipDO) {
        Assert.notNull(relationshipDO, "Saved relationship may not be null!");
        final int statusId = findRelationshipStatusId(relationshipDO.getRelationshipStatus());
        final int affectedRows = jdbcTemplate.update(INSERT_NEW_RELATIONSHIP_QUERY, relationshipDO.getSenderId(),
                relationshipDO.getReceiverId(), statusId, relationshipDO.getCreatedAt(), relationshipDO.getLastUpdated());
        if (affectedRows != 1) {
            throw new RuntimeException("Error while inserting new friend request into database");
        }
    }

    @Override
    public boolean relationshipExists(final int senderId, final int receiverId) {
        return !jdbcTemplate.queryForList("SELECT (1) FROM user_relationship " +
                        "WHERE (id_sender = ? AND id_receiver = ?)" +
                        "OR (id_sender = ? AND id_receiver = ?)",
                senderId, receiverId, receiverId, senderId).isEmpty();
    }

    @Override
    public List<IUserSearchResultDO> findNonFriendUsers_withNotBlockedRelationship_withNameLike(final String authenticatedUser,
                                                                                                final String searchString) {
        Assert.notNull(authenticatedUser, "Login of searching user may not be null");
        Assert.notNull(searchString, "Searched name may not be null");

        final var preparedString = "%" + searchString.toLowerCase() + "%";
        final List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(FIND_POTENTIAL_FRIENDS_QUERY, authenticatedUser, preparedString, authenticatedUser);
        return createSearchResultList(queryResult);
    }

    @Override
    public List<IUserSearchResultDO> findBlockedUsersByUser(@NotNull final String username) {
        Assert.notNull(username, "Username may not be null");
        final List<Map<String, Object>> queryResult = findRequestSendersToUserWithStatus(username, EnumRelationshipStatus.BLOCKED);
        return createSearchResultList(queryResult);
    }

    @Override
    public List<IUserSearchResultDO> findUsersFriends(@NotNull final String username) {
        Assert.notNull(username, "Username may not be null");
        final List<Map<String, Object>> queryResult = findUsersWithRelationshipToUser(username, EnumRelationshipStatus.FRIENDS);
        return createSearchResultList(queryResult);
    }

    @Override
    public List<IUserSearchResultDO> findPendingRequests(@NotNull final String username) {
        Assert.notNull(username, "Username may not be null");
        final List<Map<String, Object>> queryResult = findRequestSendersToUserWithStatus(username, EnumRelationshipStatus.REQUEST_SENT);
        return createSearchResultList(queryResult);
    }

    @Override
    public int removeRelationship(@NotNull final String firstUsername, @NotNull final String secondUsername) {
        Assert.notNull(firstUsername, "Username may not be null");
        Assert.notNull(secondUsername, "Username may not be null");
        return jdbcTemplate.update(REMOVE_RELATIONSHIP_QUERY, firstUsername, secondUsername);
    }

    @Override
    public Optional<IUserRelationshipDO> findRelationship(@NotNull final String firstUsername,
                                                          @NotNull final String secondUsername) {
        Assert.notNull(firstUsername, "Username may not be null");
        Assert.notNull(secondUsername, "Username may not be null");
        final List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(FIND_USER_RELATIONSHIP, firstUsername, secondUsername);
        if (queryResult.size() > 1) {
            throw new RuntimeException("Found more than one record of single relationship");
        }

        return Optional.of(createUserRelationship(queryResult.get(0)));
    }

    @Override
    public int updateRelationship(@NotNull final IUserRelationshipDO relationshipDO) {
        Assert.notNull(relationshipDO, "Updated relationship object may not be null");
        final int senderId = relationshipDO.getSenderId();
        final int receiverId = relationshipDO.getReceiverId();
        final int statusId = findRelationshipStatusId(relationshipDO.getRelationshipStatus());
        final LocalDateTime createdAt = relationshipDO.getCreatedAt();
        final LocalDateTime lastUpdated = relationshipDO.getLastUpdated();
        return jdbcTemplate.update("UPDATE user_relationship SET created_at = ?, last_updated = ?, id_status = ? " +
                        "WHERE (id_sender, id_receiver) = (?, ?)",
                createdAt, lastUpdated, statusId, senderId, receiverId);
    }

    private List<Map<String, Object>> findRequestSendersToUserWithStatus(final String username, final EnumRelationshipStatus status) {
        return jdbcTemplate.queryForList(FIND_SENDERS_OF_REQUEST_TO_USER_WITH_STATUS, findRelationshipStatusId(status), username);
    }

    private List<Map<String, Object>> findUsersWithRelationshipToUser(final String username,
                                                                      final EnumRelationshipStatus relationshipStatus) {
        return jdbcTemplate.queryForList(FIND_USERS_WITH_RELATIONSHIP_TO_USER,
                username, findRelationshipStatusId(relationshipStatus), username);
    }

    private List<IUserSearchResultDO> createSearchResultList(final List<Map<String, Object>> queryResult) {
        return queryResult.stream()
                .map(this::createSearchResult)
                .collect(Collectors.toList());
    }

    private IUserSearchResultDO createSearchResult(final Map<String, Object> row) {
        final String statusCol = (String) row.getOrDefault("status", null);
        final var status = statusCol == null ? null : EnumRelationshipStatus.valueOf(statusCol);
        return factory.createUserSearchResult((String) row.get("name"),
                                            (String) row.get("login"),
                                            status);
    }

    private IUserRelationshipDO createUserRelationship(final Map<String, Object> row) {
        final String statusCol = (String) row.get("status");
        final var status = EnumRelationshipStatus.valueOf(statusCol);
        final Timestamp createdAt = (Timestamp) row.get("created_at");
        final Timestamp lastUpdated = (Timestamp) row.get("last_updated");
        return factory.createUserRelationship((Integer) row.get("id_sender"),
                                            (Integer) row.get("id_receiver"),
                                            createdAt.toLocalDateTime(),
                                            lastUpdated.toLocalDateTime(),
                                            status);
    }

    private int findRelationshipStatusId(final EnumRelationshipStatus status) {
        if (!areRelationshipStatusesCached()) {
            cacheRelationshipStatuses();
        }

        return findRelationshipStatusIdInCache(status);
    }

    private int findRelationshipStatusIdInCache(final EnumRelationshipStatus status) {
        return relationshipStatusToIdCache.get(status);
    }

    private void cacheRelationshipStatuses() {
        relationshipStatusToIdCache = new HashMap<>();

        for (final Map<String, Object> row : jdbcTemplate.queryForList("SELECT * FROM relationship_status")) {
            cacheRelationshipStatus(row);
        }
    }

    private void cacheRelationshipStatus(final Map<String, Object> row) {
        final var status = EnumRelationshipStatus.valueOf((String) row.get("name"));
        final var statusId = (Integer) row.get("id");
        relationshipStatusToIdCache.put(status, statusId);
    }

    private boolean areRelationshipStatusesCached() {
        return relationshipStatusToIdCache != null;
    }
}
