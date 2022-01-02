package cz.markovda.friendsnet.friendship.repository.impl;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.IDOFactory;
import cz.markovda.friendsnet.friendship.dos.IUserRelationshipDO;
import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import cz.markovda.friendsnet.friendship.utils.UserRelationshipTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public class UserRelationshipRepositoryImplTest {

    private JdbcTemplate jdbcTemplate;
    private IDOFactory factory;
    private IUserRelationshipRepository userRelationshipRepository;

    @BeforeEach
    public void prepareTest() {
        jdbcTemplate = mock(JdbcTemplate.class);
        factory = mock(IDOFactory.class);
        userRelationshipRepository = new UserRelationshipRepository(jdbcTemplate, factory);
    }

    @Test
    public void createsFriendRequest() {
        final IUserRelationshipDO newFriendRequest = UserRelationshipTestUtils.prepareNewFriendRequest(1, 2, LocalDateTime.now());

        final List<Map<String, Object>> queryResult = mockRelationshipStatusCache();
        final int statusId = findRelationshipStatusId(queryResult, newFriendRequest.getRelationshipStatus());
        when(jdbcTemplate.update(UserRelationshipRepository.INSERT_NEW_RELATIONSHIP_QUERY,
                newFriendRequest.getSenderId(), newFriendRequest.getReceiverId(), statusId,
                newFriendRequest.getCreatedAt(), newFriendRequest.getLastUpdated()))
                .thenReturn(1);

        assertDoesNotThrow(() -> userRelationshipRepository.saveNewRelationship(newFriendRequest),
                "Successful insert into database should not throw exception!");
    }

    @Test
    public void throwsRuntimeException_whenErrorInSavingOccurs() {
        final IUserRelationshipDO newFriendRequest = UserRelationshipTestUtils.prepareNewFriendRequest(15, 96, LocalDateTime.now());

        final List<Map<String, Object>> queryResult = mockRelationshipStatusCache();
        final int statusId = findRelationshipStatusId(queryResult, newFriendRequest.getRelationshipStatus());
        when(jdbcTemplate.update(UserRelationshipRepository.INSERT_NEW_RELATIONSHIP_QUERY,
                newFriendRequest.getSenderId(), newFriendRequest.getReceiverId(), statusId,
                newFriendRequest.getCreatedAt(), newFriendRequest.getLastUpdated()))
                .thenReturn(0);

        assertThrows(RuntimeException.class, () -> userRelationshipRepository.saveNewRelationship(newFriendRequest),
                "Should throw when more ore less than one row is affected!");
    }

    @Test
    public void throwsWhenNullPassed_asRelationshipToSave() {
        assertThrows(IllegalArgumentException.class, () -> userRelationshipRepository.saveNewRelationship(null),
                "Should throw when null is passed to save!");
    }

    private int findRelationshipStatusId(final List<Map<String, Object>> queryResult,
                                         final EnumRelationshipStatus status) {
        for (final Map<String, Object> row : queryResult) {
            if (status.name().equals(row.get("name"))) {
                return (Integer) row.get("id");
            }
        }

        throw new RuntimeException("ID not found!");
    }

    private List<Map<String, Object>> mockRelationshipStatusCache() {
        final List<Map<String, Object>> queryResult = createRelationshipStatusQueryResult();
        when(jdbcTemplate.queryForList("SELECT * FROM relationship_status")).thenReturn(queryResult);
        return queryResult;
    }

    private List<Map<String, Object>> createRelationshipStatusQueryResult() {
        final List<Map<String, Object>> queryResult = new ArrayList<>();
        final var statuses = EnumRelationshipStatus.values();
        for (int i = 0; i < statuses.length; ++i) {
            queryResult.add(createRelationshipStatusRowMap(statuses[i], i));
        }
        return queryResult;
    }

    private Map<String, Object> createRelationshipStatusRowMap(final EnumRelationshipStatus status,
                                                               final int i) {
        final Map<String, Object> map = new HashMap<>();
        map.put("id", i);
        map.put("name", status.name());
        return map;
    }
}
