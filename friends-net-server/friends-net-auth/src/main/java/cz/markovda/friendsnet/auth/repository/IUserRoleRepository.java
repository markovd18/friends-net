package cz.markovda.friendsnet.auth.repository;

import cz.markovda.friendsnet.auth.dos.EnumUserRole;
import cz.markovda.friendsnet.auth.dos.impl.UserRoleDO;
import cz.markovda.friendsnet.auth.dos.projection.IUserRoleDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 05.01.22
 */
public interface IUserRoleRepository extends JpaRepository<UserRoleDO, Integer> {

    @Query("SELECT ar FROM auth_role ar")
    Set<IUserRoleDO> findAllReadOnly();

    Set<UserRoleDO> findAllByNameIn(final Collection<EnumUserRole> names);
}
