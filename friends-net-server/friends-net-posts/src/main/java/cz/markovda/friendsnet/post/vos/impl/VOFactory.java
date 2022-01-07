package cz.markovda.friendsnet.post.vos.impl;

import cz.markovda.friendsnet.post.vos.INewPostVO;
import cz.markovda.friendsnet.post.vos.IVOFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
@Component("postsVoFactory")
public class VOFactory implements IVOFactory {

    @Override
    public INewPostVO createNewPostVO(final String title,
                                      final String content,
                                      final LocalDateTime dateCreated,
                                      final boolean isAnnouncement) {
        return new NewPostVO(title, content, dateCreated, isAnnouncement);
    }
}
