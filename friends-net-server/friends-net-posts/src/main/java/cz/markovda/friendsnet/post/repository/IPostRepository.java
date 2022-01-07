package cz.markovda.friendsnet.post.repository;

import cz.markovda.friendsnet.post.dos.PostDO;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
public interface IPostRepository extends JpaRepository<PostDO, Integer> {
}
