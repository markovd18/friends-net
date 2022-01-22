package cz.markovda.friendsnet.friendship.repository;

import cz.markovda.friendsnet.friendship.dos.RelationshipStatusDO;
import cz.markovda.friendsnet.friendship.dos.projection.IRelationshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 05.01.22
 */

public interface IRelationshipStatusRepository extends JpaRepository<RelationshipStatusDO, Integer> {


    @Query("SELECT rs FROM relationship_status rs")
    Set<IRelationshipStatus> findAllReadOnly();
}
