package cz.markovda.friendsnet.friendship.repository;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.UserRelationshipDO;
import cz.markovda.friendsnet.friendship.dos.UserRelationshipId;
import cz.markovda.friendsnet.friendship.dos.projection.IUserSearchResultDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 05.01.22
 */
public interface IUserRelationshipRepository extends JpaRepository<UserRelationshipDO, UserRelationshipId> {

    @Modifying
    @Query("DELETE FROM user_relationship ur WHERE ur.id.idSender IN :ids AND ur.id.idReceiver IN :ids")
    int removeRelationship(@Param("ids") Collection<Integer> ids);

    @Query("SELECT ur FROM user_relationship ur " +
            "WHERE (ur.sender.login = :firstLogin OR ur.sender.login = :secondLogin) " +
            "AND (ur.receiver.login = :firstLogin OR ur.receiver.login = :secondLogin)")
    Optional<UserRelationshipDO> findRelationship(@Param("firstLogin") String firstLogin, @Param("secondLogin") String secondLogin);

    @Query("SELECT ur FROM user_relationship ur " +
            "JOIN FETCH ur.sender " +
            "WHERE (ur.sender.login = :firstLogin OR ur.sender.login = :secondLogin) " +
            "AND (ur.receiver.login = :firstLogin OR ur.receiver.login = :secondLogin)")
    Optional<UserRelationshipDO> findRelationshipFetchSender(@Param("firstLogin") String firstLogin, @Param("secondLogin") String secondLogin);

    @Query("SELECT ur FROM user_relationship ur " +
            "JOIN FETCH ur.sender " +
            "JOIN FETCH ur.status " +
            "WHERE (ur.sender.login = :firstLogin OR ur.sender.login = :secondLogin) " +
            "AND (ur.receiver.login = :firstLogin OR ur.receiver.login = :secondLogin)")
    Optional<UserRelationshipDO> findRelationshipFetchSenderFetchStatus(@Param("firstLogin") String firstLogin, @Param("secondLogin") String secondLogin);

    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN TRUE ELSE FALSE END " +
            "FROM user_relationship ur " +
            "WHERE (ur.sender.login = :firstLogin OR ur.sender.login = :secondLogin) " +
            "AND (ur.receiver.login = :firstLogin OR ur.receiver.login = :secondLogin)")
    boolean existsByUsernames(String firstLogin, String secondLogin);

    @Query(value = "SELECT au.name as name, au.login as login, rs.name as relationshipStatus FROM auth_user au " +
            "LEFT JOIN user_relationship ur ON au.id IN (ur.id_sender, ur.id_receiver) " +
            "AND (SELECT id from auth_user WHERE login = :authUsername) IN (ur.id_sender, ur.id_receiver) " +
            "LEFT JOIN relationship_status rs ON rs.id = ur.id_status " +
            "WHERE lower(au.name) LIKE :soughtName AND (rs IS NULL OR rs.name = 'REQUEST_SENT')", nativeQuery = true)
    List<IUserSearchResultDO> findPotentialFriendsWithNameLike(@Param("authUsername") String authUsername,
                                                               @Param("soughtName") String soughtNameLike);

    @Query(value = "SELECT au.login as login, au.name as name, 'FRIENDS' as relationshipStatus " +
            "FROM auth_user au " +
            "INNER JOIN user_relationship ur ON au.id IN (ur.id_sender, ur.id_receiver) " +
                "AND (SELECT id from auth_user WHERE login = :username) IN (ur.id_sender, ur.id_receiver)" +
                "AND au.login != :username " +
            "INNER JOIN relationship_status rs ON rs.id = ur.id_status " +
            "WHERE rs.name = 'FRIENDS'", nativeQuery = true)
    List<IUserSearchResultDO> findUsersFriends(@Param("username") String username);

    @Query(value = "SELECT au.login as login, au.name as name FROM auth_user au " +
            "INNER JOIN user_relationship ur on au.id IN (ur.id_sender, ur.id_receiver) " +
                "AND (SELECT id from auth_user WHERE login = :userLogin) IN (ur.id_sender, ur.id_receiver) " +
                "AND au.login != :userLogin " +
            "INNER JOIN relationship_status rs ON rs.id = ur.id_status " +
            "WHERE rs.name = 'FRIENDS' AND au.login IN :usernames", nativeQuery = true)
    List<IUserSearchResultDO> findUsersFriendsByUsernameIn(@Param("userLogin") String userLogin, @Param("usernames") Set<String> usernames);

    @Query("SELECT au.login as login, au.name as name, ur.status.name as relationshipStatus " +
            "FROM auth_user au INNER JOIN user_relationship ur ON au.id = ur.id.idSender " +
            "WHERE ur.receiver.login = :username AND ur.status.name = :statusName")
    List<IUserSearchResultDO> findUsersWithRelationshipToUser(@Param("username") String username,
                                                              @Param("statusName") EnumRelationshipStatus status);

}
