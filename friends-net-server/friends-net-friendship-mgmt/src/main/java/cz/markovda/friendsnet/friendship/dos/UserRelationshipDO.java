package cz.markovda.friendsnet.friendship.dos;

import cz.markovda.friendsnet.auth.dos.impl.UserDO;
import io.jsonwebtoken.lang.Assert;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 05.01.22
 */
@ToString
@Getter
@Entity(name = UserRelationshipDO.TABLE_NAME)
public class UserRelationshipDO {

    public static final String TABLE_NAME = "user_relationship";
    public static final String ID_SENDER = "id_sender";
    public static final String ID_RECEIVER = "id_receiver";
    public static final String CREATED_AT = "created_at";
    public static final String LAST_UPDATED = "last_updated";
    public static final String ID_STATUS = "id_status";

    @EmbeddedId
    private UserRelationshipId id;

    @MapsId("idSender")
    @JoinColumn(name = ID_SENDER, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @ToString.Exclude
    private UserDO sender;

    @MapsId("idReceiver")
    @JoinColumn(name = ID_RECEIVER, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @ToString.Exclude
    private UserDO receiver;

    @Column(name = CREATED_AT, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = LAST_UPDATED, nullable = false)
    private LocalDateTime lastUpdated;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = ID_STATUS, nullable = false)
    @ToString.Exclude
    private RelationshipStatusDO status;

    public UserRelationshipDO() {
    }

    public UserRelationshipDO(final UserDO sender, final UserDO receiver, final RelationshipStatusDO status) {
        this.id = new UserRelationshipId();
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public void setStatus(@NotNull final RelationshipStatusDO status) {
        Assert.notNull(status, "Status may not be null");
        if (this.status.isSameAs(status)) {
            return;
        }

        checkStatusIntegrity(status);
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }

    private void checkStatusIntegrity(RelationshipStatusDO status) {
        if (this.status.isBlocked()) {
            throw new IllegalStateException("Cannot change status of blocked relationship");
        }
        if (this.status.isFriends() && status.isRequestSent()) {
            throw new IllegalStateException("Cannot change status from FRIENDS to REQUEST_SENT");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserRelationshipDO that = (UserRelationshipDO) o;
        return id != null && Objects.equals(id, that.id)
                && sender != null && Objects.equals(sender, that.sender)
                && receiver != null && Objects.equals(receiver, that.receiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                sender,
                receiver);
    }
}
