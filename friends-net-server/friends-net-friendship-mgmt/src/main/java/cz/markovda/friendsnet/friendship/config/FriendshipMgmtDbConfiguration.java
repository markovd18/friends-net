package cz.markovda.friendsnet.friendship.config;

import cz.markovda.friendsnet.friendship.dos.IUserRelationshipDO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
@Configuration
@RequiredArgsConstructor
public class FriendshipMgmtDbConfiguration implements InitializingBean {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() {
        if (areNoDefaultRelationshipStatusesSet()) {
            createDefaultRelationshipStatuses();
        }
    }

    private void createDefaultRelationshipStatuses() {
        for (var status : IUserRelationshipDO.EnumRelationshipStatus.values()) {
            jdbcTemplate.update("INSERT INTO relationship_status(id, name) VALUES (DEFAULT, ?)", status.name());
        }
    }

    private boolean areNoDefaultRelationshipStatusesSet() {
        return jdbcTemplate.queryForList("SELECT (1) FROM relationship_status").isEmpty();
    }
}
