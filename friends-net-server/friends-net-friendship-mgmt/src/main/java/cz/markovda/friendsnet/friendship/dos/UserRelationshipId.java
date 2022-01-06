package cz.markovda.friendsnet.friendship.dos;

import lombok.Getter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 05.01.22
 */
@Getter
@Embeddable
public class UserRelationshipId implements Serializable {

    @Column(name = "id_sender", nullable = false, updatable = false, insertable = false)
    private Integer idSender;
    @Column(name = "id_receiver", nullable = false, updatable = false, insertable = false)
    private Integer idReceiver;

    public UserRelationshipId() {
    }

    public UserRelationshipId(@NotNull Integer idSender, @NotNull Integer idReceiver) {
        if (Objects.equals(idSender, idReceiver)) {
            throw new IllegalArgumentException("Sender may not be the receiver of the relationship request");
        }

        this.idSender = idSender;
        this.idReceiver = idReceiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserRelationshipId that = (UserRelationshipId) o;
        return idSender != null && Objects.equals(idSender, that.idSender)
                && idReceiver != null && Objects.equals(idReceiver, that.idReceiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSender, idReceiver);
    }
}
