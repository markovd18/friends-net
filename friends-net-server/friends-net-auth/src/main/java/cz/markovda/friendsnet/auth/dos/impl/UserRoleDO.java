package cz.markovda.friendsnet.auth.dos.impl;

import cz.markovda.friendsnet.auth.dos.EnumUserRole;
import cz.markovda.friendsnet.auth.dos.projection.IUserRoleDO;
import lombok.Getter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 05.01.22
 */
@Getter
@Entity(name = UserRoleDO.TABLE_NAME)
public class UserRoleDO implements IUserRoleDO {

    public static final String TABLE_NAME = "auth_role";
    public static final String SEQUENCE_NAME = "auth_role_id_seq";
    public static final String NAME = "name";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = NAME, nullable = false, unique = true, length = 15)
    private EnumUserRole name;

    public UserRoleDO() {
    }

    public UserRoleDO(final EnumUserRole name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserRoleDO that = (UserRoleDO) o;
        return Objects.equals(id, that.id) &&
                name == that.name;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public int getId() {
        return id;
    }
}
