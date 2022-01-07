package cz.markovda.friendsnet.post.service;

import cz.markovda.friendsnet.post.vos.INewPostVO;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
public interface IPostService {

    @Transactional
    void createNewPost(@NotNull INewPostVO post);
}
