package cz.markovda.friendsnet.friendship.service.impl;

import cz.markovda.friendsnet.auth.dos.impl.UserDO;
import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.service.IAuthenticationService;
import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.RelationshipStatusDO;
import cz.markovda.friendsnet.friendship.dos.UserRelationshipDO;
import cz.markovda.friendsnet.friendship.repository.IRelationshipStatusRepository;
import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import cz.markovda.friendsnet.friendship.service.IRelationshipStatusService;
import cz.markovda.friendsnet.friendship.service.IUserRelationshipService;
import io.jsonwebtoken.lang.Assert;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
    private final IRelationshipStatusService relationshipStatusService;
    private final IRelationshipStatusRepository relationshipStatusRepository;

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
        final Set<Integer> userIds = getSenderAndReceiverIds(authenticatedLogin, otherLogin);

        final int deletedRows = userRelationshipRepository.removeRelationship(userIds);
        if (deletedRows != 1) {
            throw new IllegalStateException("Expected to delete 1 row, but actually deleted " + deletedRows);
        }
        log.debug("End of removeRelationship method.");
    }

    @Transactional
    @Override
    public void acceptFriendRequest(@NotNull final String senderLogin) {
        log.debug("Start of acceptFriendRequest method (args: {}).", senderLogin);
        Assert.notNull(senderLogin, "Friend request sender login may not be null");
        if (authenticationService.isUserAnonymous()) {
            throw new AccessDeniedException("Anonymous user may not update relationships");
        }

        final String authenticatedLogin = authenticationService.getLoginName();
        final UserRelationshipDO relationshipDO = getUserRelationship(authenticatedLogin, senderLogin,
                userRelationshipRepository::findRelationship);
        final RelationshipStatusDO friendsStatus = getRelationshipStatus(EnumRelationshipStatus.FRIENDS);
        relationshipDO.setStatus(friendsStatus);

        log.debug("End of acceptFriendRequest method.");
    }

    private RelationshipStatusDO getRelationshipStatus(final EnumRelationshipStatus status) {
        final Integer statusId = relationshipStatusService.getRelationshipStatusId(status);
        return relationshipStatusRepository.getById(statusId);
    }

    @Transactional
    @Override
    public void blockUser(@NotNull final String username) {
        log.debug("Start of blockUser method (args: {}).", username);
        Assert.notNull(username, "Friend request sender login may not be null");
        if (authenticationService.isUserAnonymous()) {
            throw new AccessDeniedException("Anonymous user may not update relationships");
        }

        final String authenticatedLogin = authenticationService.getLoginName();
        final UserRelationshipDO relationshipDO = getUserRelationship(authenticatedLogin, username,
                userRelationshipRepository::findRelationshipFetchSender);

        if (authenticatedLogin.equals(relationshipDO.getSender().getLogin())) {
            throw new AccessDeniedException("Only receiver of request may block sender");
        }

        final RelationshipStatusDO blockedStatus = getRelationshipStatus(EnumRelationshipStatus.BLOCKED);
        relationshipDO.setStatus(blockedStatus);

        log.debug("End of blockUser method.");
    }

    @Transactional
    @Override
    public void unblockUser(@NotNull final String username) {
        log.debug("Start of unblockUser method (args: {}).", username);
        Assert.notNull(username, "Friend request sender login may not be null");
        if (authenticationService.isUserAnonymous()) {
            throw new AccessDeniedException("Anonymous user may not update relationships");
        }

        final String authenticatedLogin = authenticationService.getLoginName();
        final UserRelationshipDO relationshipDO = getUserRelationship(authenticatedLogin, username,
                userRelationshipRepository::findRelationshipFetchSenderFetchStatus);
        validateRelationshipStateForUnblocking(authenticatedLogin, relationshipDO);

        userRelationshipRepository.delete(relationshipDO);

        log.debug("End of unblockUser method.");
    }

    private void validateRelationshipStateForUnblocking(String authenticatedLogin, UserRelationshipDO relationshipDO) {
        if (!relationshipDO.getStatus().isBlocked()) {
            throw new IllegalStateException("Only blocked relationship may be unblocked!");
        }
        if (authenticatedLogin.equals(relationshipDO.getSender().getLogin())) {
            throw new AccessDeniedException("Only receiver of request may unblock sender");
        }
    }

    private void saveNewRelationship(final String receiverName, final String senderLogin) {
        if (userRelationshipRepository.existsByUsernames(senderLogin, receiverName)) {
            throw new IllegalStateException("Cannot recreate existing relationship!");
        }

        final RelatedUsers relatedUsers = getSenderAndReceiver(receiverName, senderLogin);
        final var relationship = new UserRelationshipDO(relatedUsers.sender, relatedUsers.receiver,
                getRelationshipStatus(EnumRelationshipStatus.REQUEST_SENT));
        userRelationshipRepository.save(relationship);
    }

    private RelatedUsers getSenderAndReceiver(final String receiverLogin, final String senderLogin) {
        final Set<Integer> ids = getSenderAndReceiverIds(receiverLogin, senderLogin);
        final Set<UserDO> users = ids.stream()
                .map(userRepository::getById)
                .collect(Collectors.toSet());

        return createRelatedUsersFromUserSet(receiverLogin, senderLogin, users);
    }

    private RelatedUsers createRelatedUsersFromUserSet(String receiverLogin, String senderLogin, Set<UserDO> users) {
        final var relatedUsers = new RelatedUsers();
        for (final UserDO user : users) {
            if (user.getLogin().equals(senderLogin)) {
                relatedUsers.setSender(user);
                continue;
            }
            if (user.getLogin().equals(receiverLogin)) {
                relatedUsers.setReceiver(user);
            }
        }

        return relatedUsers;
    }

    private Set<Integer> getSenderAndReceiverIds(String receiverLogin, String senderLogin) {
        final Set<Integer> ids = userRepository.findIdsByLoginIn(Set.of(receiverLogin, senderLogin));
        if (ids.size() != 2) {
            throw new IllegalStateException("More than two ID's found for usernames " + receiverLogin + " and " + senderLogin);
        }
        return ids;
    }

    private UserRelationshipDO getUserRelationship(final String firstUsername,
                                                   final String secondUsername,
                                                   final BiFunction<String, String, Optional<UserRelationshipDO>> repositoryQuery) {
        return repositoryQuery.apply(firstUsername, secondUsername)
                .orElseThrow(() -> new IllegalArgumentException("Relationship between users " + firstUsername +
                        " and " + secondUsername + " does not exist"));
    }

    @Getter
    @Setter
    private static class RelatedUsers {
        UserDO sender;
        UserDO receiver;
    }
}
