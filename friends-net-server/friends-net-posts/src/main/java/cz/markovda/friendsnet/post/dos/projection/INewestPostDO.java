package cz.markovda.friendsnet.post.dos.projection;

import javax.persistence.Lob;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 19.01.22
 */
public interface INewestPostDO {

    String getAuthorName();

    String getAuthorLogin();

    String getTitle();

    String getContent();

    LocalDateTime getDateCreated();

    boolean isAnnouncement();
}
