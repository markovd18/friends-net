package cz.markovda.friendsnet.friendship.service.impl;

import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.service.IAuthenticationService;
import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.IDOFactory;
import cz.markovda.friendsnet.friendship.dos.IUserRelationshipDO;
import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import cz.markovda.friendsnet.friendship.service.IUserRelationshipService;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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

    @Transactional
    @Override
    public void removeRelationship(@NotNull final String otherLogin) {
        log.debug("Start of removeRelationship method (args: {}).", otherLogin);
        Assert.notNull(otherLogin, "Login may not be null!");
        if (authenticationService.isUserAnonymous()) {
            throw new AccessDeniedException("Anonymous user may not remove relationships");
        }

        final String authenticatedLogin = authenticationService.getLoginName();
        final int deletedRows = userRelationshipRepository.removeRelationship(authenticatedLogin, otherLogin);
        if (deletedRows != 1) {
            throw new IllegalStateException("Expected to delete 1 row, but actually deleted " + deletedRows);
        }
        log.debug("End of removeRelationship method.");
    }

    @Override
    public void acceptFriendRequest(@NotNull final String senderLogin) {
        log.debug("Start of acceptFriendRequest method (args: {}).", senderLogin);
        Assert.notNull(senderLogin, "Friend request sender login may not be null");
        if (authenticationService.isUserAnonymous()) {
            throw new AccessDeniedException("Anonymous user may not update relationships");
        }

        final String authenticatedLogin = authenticationService.getLoginName();
        final IUserRelationshipDO relationshipDO = getUserRelationship(authenticatedLogin, senderLogin);
        relationshipDO.acceptRequest(LocalDateTime.now(clock));
        final int affectedRows = userRelationshipRepository.updateRelationship(relationshipDO);
        if (affectedRows != 1) {
            throw new IllegalStateException("Expected to update single relationship, but actually updated " + affectedRows);
        }
        log.debug("End of acceptFriendRequest method.");
    }

    @Override
    public void blockUser(@NotNull final String username) {
        log.debug("Start of blockUser method (args: {}).", username);
        Assert.notNull(username, "Friend request sender login may not be null");
        if (authenticationService.isUserAnonymous()) {
            throw new AccessDeniedException("Anonymous user may not update relationships");
        }

        final String authenticatedLogin = authenticationService.getLoginName();
        final IUserRelationshipDO relationshipDO = getUserRelationship(authenticatedLogin, username);
        if (getUserId(authenticatedLogin) != relationshipDO.getReceiverId()) {
            throw new AccessDeniedException("Only receiver of request may block sender");
        }

        relationshipDO.block(LocalDateTime.now(clock));
        final int affectedRows = userRelationshipRepository.updateRelationship(relationshipDO);
        if (affectedRows != 1) {
            throw new IllegalStateException("Expected to update single relationship, but actually updated " + affectedRows);
        }
        log.debug("End of blockUser method.");
    }

    @Override
    public void unblockUser(@NotNull final String username) {
        log.debug("Start of unblockUser method (args: {}).", username);
        Assert.notNull(username, "Friend request sender login may not be null");
        if (authenticationService.isUserAnonymous()) {
            throw new AccessDeniedException("Anonymous user may not update relationships");
        }

        final String authenticatedLogin = authenticationService.getLoginName();
        final IUserRelationshipDO relationshipDO = getUserRelationship(authenticatedLogin, username);
        if (relationshipDO.getRelationshipStatus() != EnumRelationshipStatus.BLOCKED) {
            throw new IllegalStateException("Only blocked relationship may be unblocked!");
        }
        if (getUserId(authenticatedLogin) != relationshipDO.getReceiverId()) {
            throw new AccessDeniedException("Only receiver of request may unblock sender");
        }

        final int affectedRows = userRelationshipRepository.removeRelationship(authenticatedLogin, username);
        if (affectedRows != 1) {
            throw new IllegalStateException("Expected to update single relationship, but actually updated " + affectedRows);
        }
        log.debug("End of unblockUser method.");
    }

    private void saveNewRelationship(final String receiverName, final String senderLogin) {
        final int senderId = getUserId(senderLogin);
        final int receiverId = getUserId(receiverName);
        if (userRelationshipRepository.relationshipExists(senderId, receiverId)) {
            throw new IllegalStateException("Cannot recreate existing relationship!");
        }
        userRelationshipRepository.saveNewRelationship(doFactory.createUserRelationship(senderId, receiverId, LocalDateTime.now(clock)));
    }

    private Integer getUserId(final String login) {
        return userRepository.findUserId(login)
                .orElseThrow(() -> new IllegalArgumentException("ID of user " + login + " not found!"));
    }

    private IUserRelationshipDO getUserRelationship(final String firstUsername, final String secondUsername) {
        return userRelationshipRepository.findRelationship(firstUsername, secondUsername)
                .orElseThrow(() -> new IllegalArgumentException("Relationship between users " + firstUsername +
                        " and " + secondUsername + " does not exist"));
    }
}
