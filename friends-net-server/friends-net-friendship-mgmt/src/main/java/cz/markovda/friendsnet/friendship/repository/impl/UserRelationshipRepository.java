package cz.markovda.friendsnet.friendship.repository.impl;

import cz.markovda.friendsnet.friendship.dos.IUserRelationshipDO;
import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
@Repository
@RequiredArgsConstructor
public class UserRelationshipRepository implements IUserRelationshipRepository {

    private final JdbcTemplate jdbcTemplate;

    private Map<IUserRelationshipDO.EnumRelationshipStatus, Integer> relationshipStatusToIdCache;

    protected static final String INSERT_NEW_RELATIONSHIP_QUERY = "INSERT INTO user_relationship(" +
            "id_sender, id_receiver, id_status, created_at, last_updated) VALUES (?, ?, ?, ?, ?)";

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

    private int findRelationshipStatusId(final IUserRelationshipDO.EnumRelationshipStatus status) {
        if (!areRelationshipStatusesCached()) {
            cacheRelationshipStatuses();
        }

        return findRelationshipStatusIdInCache(status);
    }

    private int findRelationshipStatusIdInCache(final IUserRelationshipDO.EnumRelationshipStatus status) {
        return relationshipStatusToIdCache.get(status);
    }

    private void cacheRelationshipStatuses() {
        relationshipStatusToIdCache = new HashMap<>();

        for (final Map<String, Object> row : jdbcTemplate.queryForList("SELECT * FROM relationship_status")) {
            cacheRelationshipStatus(row);
        }
    }

    private void cacheRelationshipStatus(final Map<String, Object> row) {
        final var status = IUserRelationshipDO.EnumRelationshipStatus.valueOf((String) row.get("name"));
        final var statusId = (Integer) row.get("id");
        relationshipStatusToIdCache.put(status, statusId);
    }

    private boolean areRelationshipStatusesCached() {
        return relationshipStatusToIdCache != null;
    }
}
