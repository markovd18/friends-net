package cz.markovda.friendsnet.auth.dos.impl;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 24.12.21
 */

@ToString
@Builder
@Getter
@Entity(name = UserDO.TABLE_NAME)
public class UserDO {

    public static final String TABLE_NAME = "auth_user";
    public static final String SEQUENCE_NAME = "auth_user_id_seq";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";
    public static final String AUTH_USER_ROLE_TABLE = "auth_user_role";
    public static final String ID_USER = "id_user";
    public static final String ID_ROLE = "id_role";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
    @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Integer id;

    @Email
    @Column(name = LOGIN, nullable = false, unique = true, length = 50)
    private String login;

    @Column(name = PASSWORD, nullable = false, length = 70)
    private String password;

    @NotNull
    @Column(name = NAME, nullable = false, length = 50)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = AUTH_USER_ROLE_TABLE,
            joinColumns = @JoinColumn(name = ID_USER),
            inverseJoinColumns = @JoinColumn(name = ID_ROLE))
    @ToString.Exclude
    private Set<UserRoleDO> roles;

    protected UserDO() {
        this(null, null);
    }

    protected UserDO(final String login, final String password) {
        this((Integer) null, login, password);
    }

    public UserDO(final String login, final String password, final String name) {
        this(null, login, password, name, new LinkedHashSet<>());
    }
    protected UserDO(final Integer id, final String login, final String password) {
        this(id, login, password, new LinkedHashSet<>());
    }

    protected UserDO(final Integer id, final String login, final String password, final Set<UserRoleDO> roles) {
        this(id, login, password, null, roles);
    }

    public UserDO(final String login, final String password, final String name, final Set<UserRoleDO> roles) {
        this(null, login, password, name, roles);
    }

    protected UserDO(final Integer id, final String login, final String password, final String name, final Set<UserRoleDO> roles) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.roles = roles;
    }

    public Set<UserRoleDO> getRoles() {
        return Set.copyOf(roles);
    }

    public void addRole(final UserRoleDO role) {
        roles.add(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserDO userDO = (UserDO) o;
        return id != null && Objects.equals(id, userDO.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
