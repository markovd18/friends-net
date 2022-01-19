package cz.markovda.friendsnet.post.service;

import cz.markovda.friendsnet.post.vos.INewPostVO;
import cz.markovda.friendsnet.post.vos.IPostVO;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
public interface IPostService {

    @Transactional
    void createNewPost(@NotNull INewPostVO post);

    @Transactional(readOnly = true)
    List<IPostVO> findNewestPosts(@Nullable Integer max, @Nullable LocalDateTime newerThan);
}
