package cz.markovda.friendsnet.post.vos;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
public interface IVOFactory {

    INewPostVO createNewPostVO(String content, LocalDateTime dateCreated, boolean isAnnouncement);
}
