package cz.markovda.friendsnet.post.vos.impl;

import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.friendsnet.post.vos.IPostVO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
public record PostVO(
        @NotNull IUserVO author,
        @NotNull String content,
        @NotNull LocalDateTime dateCreated,
        boolean isAnnouncement
) implements IPostVO {

    @Override
    public IUserVO getAuthor() {
        return author;
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
