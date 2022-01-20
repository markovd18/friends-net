package cz.markovda.friendsnet.post.repository;

import cz.markovda.friendsnet.post.dos.PostDO;
import cz.markovda.friendsnet.post.dos.projection.INewestPostDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
public interface IPostRepository extends JpaRepository<PostDO, Integer> {

    @Query(value = "select distinct p.date_created as dateCreated, " +
            "p.content as content, " +
            "p.announcement as announcement, au.login as authorLogin, au.name as authorName from post p " +
            "left join user_relationship ur on p.author_id in (ur.id_sender, ur.id_receiver) and " +
                "(select id from auth_user where login = :login) in (ur.id_sender, ur.id_receiver) " +
            "left join relationship_status rs on ur.id_status = rs.id " +
            "left join auth_user au on p.author_id = au.id " +
            "where (p.announcement = true or rs.name = 'FRIENDS' or au.login = :login) and (p.date_created > :dateLimit) and (p.author_id = au.id) " +
            "order by p.date_created desc", nativeQuery = true)
    Page<INewestPostDO> findNewestPosts(@Param("dateLimit") LocalDateTime dateLimit, @Param("login") String login, Pageable pageable);
}
