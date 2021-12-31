package cz.markovda.friendsnet.friendship.service.impl;

import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.service.IAuthenticationService;
import cz.markovda.friendsnet.friendship.dos.IDOFactory;
import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import cz.markovda.friendsnet.friendship.service.IUserRelationshipService;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserRelationshipService implements IUserRelationshipService {

    private final IAuthenticationService authenticationService;
    private final IUserRepository userRepository;
    private final IUserRelationshipRepository userRelationshipRepository;
    private final IDOFactory doFactory;
    private final Clock clock;

    @Override
    public void createNewRelationship(@NotNull final String receiverName) {
        log.debug("Start for createNewRelationship method (args: {}).", receiverName);
        Assert.notNull(receiverName, "Receiver name may not be null!");
        if (authenticationService.isUserAnonymous()) {
            throw new AccessDeniedException("Anonymous user may not create relationships");
        }

        final String senderLogin = authenticationService.getLoginName();
        if (senderLogin.equals(receiverName)) {
            throw new IllegalArgumentException("User cannot create relationship with themselves!");
        }

        saveNewRelationship(receiverName, senderLogin);
        log.debug("End of createNewRelationship method.");
    }

    private void saveNewRelationship(final String receiverName, final String senderLogin) {
        final int senderId = getUserId(senderLogin);
        final int receiverId = getUserId(receiverName);
        userRelationshipRepository.saveNewRelationship(doFactory.createUserRelationship(senderId, receiverId, LocalDateTime.now(clock)));
    }

    private Integer getUserId(final String login) {
        return userRepository.findUserId(login)
                .orElseThrow(() -> new IllegalArgumentException("ID of user " + login + " not found!"));
    }
}
