package cz.markovda.friendsnet.friendship.service.impl;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.projection.IRelationshipStatus;
import cz.markovda.friendsnet.friendship.repository.IRelationshipStatusRepository;
import cz.markovda.friendsnet.friendship.service.IRelationshipStatusService;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 05.01.22
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RelationshipStatusService implements IRelationshipStatusService {

    private final IRelationshipStatusRepository relationshipStatusRepository;

    private Map<EnumRelationshipStatus, Integer> relationshipStatusToIdCache;

    @Override
    public Integer getRelationshipStatusId(@NotNull final EnumRelationshipStatus name) {
        log.debug("Start of getRelationshipStatusId method (args: {}).", name);
        Assert.notNull(name, "Status name may not be null");
        if (!areRelationshipStatusesCached()) {
            cacheRelationshipStatuses();
        }

        return findRelationshipStatusIdInCache(name);
    }

    private Integer findRelationshipStatusIdInCache(final EnumRelationshipStatus status) {
        log.debug("Returning {} status ID from cache.", status);
        return relationshipStatusToIdCache.get(status);
    }

    private void cacheRelationshipStatuses() {
        log.debug("Statuses not cached. Caching from DB...");
        relationshipStatusToIdCache = new HashMap<>();

        final Set<IRelationshipStatus> statuses = relationshipStatusRepository.findAllReadOnly();
        for (final var status : statuses) {
            cacheRelationshipStatus(status);
        }
        log.debug("Statuses cached.");
    }

    private void cacheRelationshipStatus(final IRelationshipStatus status) {
        relationshipStatusToIdCache.put(status.getName(), status.getId());
    }

    private boolean areRelationshipStatusesCached() {
        return relationshipStatusToIdCache != null;
    }
}
