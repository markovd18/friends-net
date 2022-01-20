package cz.markovda.friendsnet.post.vos;

import cz.markovda.friendsnet.auth.vos.IUserVO;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
public interface IPostVO {

    IUserVO getAuthor();

    String getContent();

    LocalDateTime getDateCreated();

    boolean isAnnouncement();
}
