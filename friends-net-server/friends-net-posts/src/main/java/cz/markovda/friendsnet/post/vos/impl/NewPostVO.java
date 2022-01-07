package cz.markovda.friendsnet.post.vos.impl;

import cz.markovda.friendsnet.post.vos.INewPostVO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
public record NewPostVO(
        @NotNull @Length(min = 3, max = 255) String title,
        @NotNull String content,
        @NotNull LocalDateTime dateCreated,
        boolean isAnnouncement
) implements INewPostVO {

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
