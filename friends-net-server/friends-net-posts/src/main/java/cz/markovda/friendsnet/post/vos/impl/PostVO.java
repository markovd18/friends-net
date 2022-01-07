package cz.markovda.friendsnet.post.vos.impl;

import cz.markovda.friendsnet.auth.vos.impl.UserVO;
import cz.markovda.friendsnet.post.vos.IPostVO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
public record PostVO(
        @NotNull UserVO author,
        @NotNull @Length(min = 3, max = 255) String title,
        @NotNull String content,
        @NotNull LocalDateTime dateCreated,
        boolean isAnnouncement
) implements IPostVO {

    @Override
    public UserVO getAuthor() {
        return author;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    @Override
    public boolean isAnnouncement() {
        return isAnnouncement;
    }
}
