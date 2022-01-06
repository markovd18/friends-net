package cz.markovda.friendsnet.friendship.service.impl;

import cz.markovda.friendsnet.auth.dos.impl.UserDO;
import cz.markovda.friendsnet.auth.repository.IUserJpaRepository;
import cz.markovda.friendsnet.auth.service.IAuthenticationService;
import cz.markovda.friendsnet.friendship.dos.EnumRelationshipStatus;
import cz.markovda.friendsnet.friendship.dos.RelationshipStatusDO;
import cz.markovda.friendsnet.friendship.repository.IRelationshipStatusRepository;
import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import cz.markovda.friendsnet.friendship.service.IRelationshipStatusService;
import cz.markovda.friendsnet.friendship.service.IUserRelationshipService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 31.12.21
 */
public class UserRelationshipServiceTest {

    private IUserRelationshipService userRelationshipService;
    private IAuthenticationService authenticationService;
    private IUserJpaRepository userRepository;
    private IUserRelationshipRepository userRelationshipRepository;
    private static IRelationshipStatusService relationshipStatusService;
    private static IRelationshipStatusRepository relationshipStatusRepository;

    public static final int REQUEST_SENT_ID = 1;
    public static final int FRIENDS_ID = 2;
    public static final int BLOCKED_ID = 3;

    @BeforeAll
    static void prepareStatuses() {
        relationshipStatusService = mock(IRelationshipStatusService.class);
        relationshipStatusRepository = mock(IRelationshipStatusRepository.class);

        final var requestSentStatus = new RelationshipStatusDO(EnumRelationshipStatus.REQUEST_SENT);
        final var friendsStatus = new RelationshipStatusDO(EnumRelationshipStatus.FRIENDS);
        final var blockedStatus = new RelationshipStatusDO(EnumRelationshipStatus.BLOCKED);

        when(relationshipStatusService.getRelationshipStatusId(EnumRelationshipStatus.REQUEST_SENT)).thenReturn(REQUEST_SENT_ID);
        when(relationshipStatusService.getRelationshipStatusId(EnumRelationshipStatus.FRIENDS)).thenReturn(FRIENDS_ID);
        when(relationshipStatusService.getRelationshipStatusId(EnumRelationshipStatus.BLOCKED)).thenReturn(BLOCKED_ID);

        when(relationshipStatusRepository.getById(REQUEST_SENT_ID)).thenReturn(requestSentStatus);
        when(relationshipStatusRepository.getById(FRIENDS_ID)).thenReturn(friendsStatus);
        when(relationshipStatusRepository.getById(BLOCKED_ID)).thenReturn(blockedStatus);

        when(relationshipStatusRepository.findAllReadOnly()).thenReturn(Set.of(requestSentStatus, friendsStatus, blockedStatus));
    }

    @BeforeEach
    public void prepareTest() {
       authenticationService = mock(IAuthenticationService.class);
       userRelationshipRepository = mock(IUserRelationshipRepository.class);
       userRepository = mock(IUserJpaRepository.class);

       userRelationshipService = new UserRelationshipService(authenticationService, userRepository,
               userRelationshipRepository, relationshipStatusService, relationshipStatusRepository);
    }

    @Test
    public void createsNewRelationship_whenAuthenticated_andReceiverExists() {
        final var senderLogin = "test";
        final var senderId = 1;
        final var receiverLogin = "receiver";
        final var receiverId = 2;

        when(authenticationService.isUserAnonymous()).thenReturn(false);
        when(authenticationService.getLoginName()).thenReturn(senderLogin);
        when(userRepository.findIdsByLoginIn(Set.of(senderLogin, receiverLogin))).thenReturn(Set.of(senderId, receiverId));
        when(userRelationshipRepository.existsByUsernames(senderLogin, receiverLogin)).thenReturn(false);
        when(userRepository.getById(senderId)).thenReturn(new UserDO(senderLogin, "", "", null));
        when(userRepository.getById(receiverId)).thenReturn(new UserDO(receiverLogin, "", "", null));

        assertDoesNotThrow(() -> userRelationshipService.createNewRelationship(receiverLogin),
                "User relationship should be created when receiver exists and sender is authenticated!");
    }

    @Test
    public void throwsAccessDenied_whenNotAuthenticated() {
        when(authenticationService.isUserAnonymous()).thenReturn(true);

        assertThrows(AccessDeniedException.class, () -> userRelationshipService.createNewRelationship("throws"),
                "Creating new relationship has to throw when sender is not authenticated!");
    }

    @Test
    public void throwsIllegalState_whenReceiverOrSenderDoesNotExist() {
        final var receiverLogin = "throws";
        final var senderLogin = "me";

        when(authenticationService.isUserAnonymous()).thenReturn(false);
        when(authenticationService.getLoginName()).thenReturn(senderLogin);
        when(userRepository.findIdsByLoginIn(Set.of(senderLogin, receiverLogin))).thenReturn(Set.of(1));

        assertThrows(IllegalStateException.class, () -> userRelationshipService.createNewRelationship(receiverLogin),
                "Creating new relationship has to throw when one of user's does not exist!");
    }

    @Test
    public void throwsIllegalArgument_whenReceiverIsSender() {
        final var senderLogin = "receiver";

        when(authenticationService.isUserAnonymous()).thenReturn(false);
        when(authenticationService.getLoginName()).thenReturn(senderLogin);

        assertThrows(IllegalArgumentException.class, () -> userRelationshipService.createNewRelationship(senderLogin),
                "Creating new relationship with self has to throw!");
    }

    @Test
    public void throwsIllegalState_whenRelationshipExists() {
        final var senderLogin = "sender";
        final var receiverLogin = "receiver";

        when(authenticationService.isUserAnonymous()).thenReturn(false);
        when(authenticationService.getLoginName()).thenReturn(senderLogin);
        when(userRelationshipRepository.existsByUsernames(senderLogin, receiverLogin)).thenReturn(true);
        when(userRepository.findIdsByLoginIn(Set.of(senderLogin, receiverLogin))).thenReturn(Set.of(1, 2));

        assertThrows(IllegalStateException.class, () -> userRelationshipService.createNewRelationship(receiverLogin),
                "Attempt to recreate existing relationship should throw!");
    }

}
