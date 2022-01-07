package cz.markovda.friendsnet.post.vos;

import cz.markovda.friendsnet.auth.vos.impl.UserVO;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
public interface IPostVO {

    UserVO getAuthor();

    String getTitle();

    String getContent();

    LocalDateTime getDateCreated();

    boolean isAnnouncement();
}
