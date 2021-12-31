package cz.markovda.friendsnet.friendship.dos.impl;

import cz.markovda.friendsnet.friendship.dos.IDOFactory;
import cz.markovda.friendsnet.friendship.dos.IUserRelationshipDO;
import io.jsonwebtoken.lang.Assert;
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
}
