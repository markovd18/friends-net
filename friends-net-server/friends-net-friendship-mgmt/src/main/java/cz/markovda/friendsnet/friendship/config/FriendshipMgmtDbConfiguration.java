package cz.markovda.friendsnet.friendship.config;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.RelationshipStatusDO;
import cz.markovda.friendsnet.friendship.repository.IRelationshipStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
@Configuration
@RequiredArgsConstructor
public class FriendshipMgmtDbConfiguration implements InitializingBean {

    private final IRelationshipStatusRepository relationshipStatusRepository;

    @Transactional
    @Override
    public void afterPropertiesSet() {
        if (areNoDefaultRelationshipStatusesSet()) {
            createDefaultRelationshipStatuses();
        }
    }

    private void createDefaultRelationshipStatuses() {
        final Set<RelationshipStatusDO> statuses = createDefaultStatusesFromEnumValues();
        relationshipStatusRepository.saveAll(statuses);
    }

    private boolean areNoDefaultRelationshipStatusesSet() {
        return relationshipStatusRepository.count() == 0;
    }

    private Set<RelationshipStatusDO> createDefaultStatusesFromEnumValues() {
        return Arrays.stream(EnumRelationshipStatus.values())
                .map(RelationshipStatusDO::new)
                .collect(Collectors.toSet());
    }
}
