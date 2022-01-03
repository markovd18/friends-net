package cz.markovda.friendsnet.friendship.dos.impl;

import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.IDOFactory;
import cz.markovda.friendsnet.friendship.dos.IUserRelationshipDO;
import cz.markovda.friendsnet.friendship.dos.IUserSearchResultDO;
import io.jsonwebtoken.lang.Assert;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
@Component("UserRelationsDOFactory")
public class DOFactory implements IDOFactory {

    @Override
    public IUserRelationshipDO createUserRelationship(final int senderId,
                                                      final int receiverId,
                                                      @NotNull final LocalDateTime createdAt) {
        Assert.notNull(createdAt, "Relationship creation time may not be null!");
        return new UserRelationshipDO(senderId, receiverId, createdAt);
    }

    @Override
    public IUserRelationshipDO createUserRelationship(final int senderId, final int receiverId,
                                                      @NotNull final LocalDateTime createdAt,
                                                      @NotNull final LocalDateTime updatedAt,
                                                      @NotNull final EnumRelationshipStatus status) {
        Assert.notNull(createdAt,"Relationship creation time may not be null!");
        Assert.notNull(updatedAt, "Relationship last update time may not be null!");
        Assert.notNull(status, "Relationship status may not be null!");
        Assert.isTrue(!createdAt.isAfter(updatedAt), "Creation time may not be after last update!");
        return new UserRelationshipDO(senderId, receiverId, createdAt, updatedAt, status);
    }

    @Override
    public IUserSearchResultDO createUserSearchResult(@NotNull final String name,
                                                      @NotNull final String login,
                                                      @Nullable final EnumRelationshipStatus status) {
        Assert.notNull(name, "Name of found user may not be null!");
        Assert.notNull(login, "Login of found user may not be null!");
        return new UserSearchResultDO(name, login, status);
    }
}
