package cz.markovda.friendsnet.friendship.dos;

import cz.markovda.friendsnet.friendship.dos.projection.IRelationshipStatus;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Objects;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 05.01.22
 */
@ToString
@Getter
@Entity(name = RelationshipStatusDO.TABLE_NAME)
public class RelationshipStatusDO implements IRelationshipStatus {

    public static final String TABLE_NAME = "relationship_status";
    public static final String SEQUENCE_NAME = "relationship_status_id_seq";
    public static final String NAME = "name";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = NAME, nullable = false, unique = true)
    private EnumRelationshipStatus name;

    public RelationshipStatusDO() {
    }

    public RelationshipStatusDO(final EnumRelationshipStatus name) {
        this.name = name;
    }

    public boolean isBlocked() {
        return name == EnumRelationshipStatus.BLOCKED;
    }

    public boolean isFriends() {
        return name == EnumRelationshipStatus.FRIENDS;
    }

    public boolean isRequestSent() {
        return name == EnumRelationshipStatus.REQUEST_SENT;
    }

    public boolean isSameAs(final RelationshipStatusDO other) {
        if (other == null) {
            return false;
        }

        return name == other.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RelationshipStatusDO that = (RelationshipStatusDO) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
