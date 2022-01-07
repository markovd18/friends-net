package cz.markovda.friendsnet.auth.repository;

import cz.markovda.friendsnet.auth.dos.impl.UserDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 05.01.22
 */
public interface IUserRepository extends JpaRepository<UserDO, Integer> {

    @Transactional(readOnly = true)
    @Query("SELECT au FROM auth_user au JOIN FETCH au.roles WHERE au.login = :login")
    Optional<UserDO> findByLoginFetchRoles(@Param("login") String login);

    boolean existsByLogin(String login);

    Optional<Integer> findIdByLogin(String login);

    @Query("SELECT au.id FROM auth_user au WHERE au.login in :logins")
    Set<Integer> findIdsByLoginIn(Collection<String> logins);

    @Query("SELECT au.name FROM auth_user au WHERE au.login = :login")
    Optional<String> findNameByLogin(@Param("login") String login);
}
