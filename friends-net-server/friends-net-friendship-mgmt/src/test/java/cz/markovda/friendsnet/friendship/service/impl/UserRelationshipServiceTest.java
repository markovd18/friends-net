package cz.markovda.friendsnet.friendship.service.impl;

import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.service.IAuthenticationService;
import cz.markovda.friendsnet.friendship.dos.IDOFactory;
import cz.markovda.friendsnet.friendship.repository.IUserRelationshipRepository;
import cz.markovda.friendsnet.friendship.service.IUserRelationshipService;
import cz.markovda.friendsnet.friendship.utils.UserRelationshipTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

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
    private IUserRepository userRepository;
    private IUserRelationshipRepository userRelationshipRepository;
    private IDOFactory doFactory;
    private Clock clock;

    @BeforeEach
    public void prepareTest() {
       authenticationService = mock(IAuthenticationService.class);
       userRelationshipRepository = mock(IUserRelationshipRepository.class);
       userRepository = mock(IUserRepository.class);
       doFactory = mock(IDOFactory.class);
       clock = mock(Clock.class);
       userRelationshipService = new UserRelationshipService(authenticationService, userRepository,
               userRelationshipRepository, doFactory, clock);
    }

    @Test
    public void createsNewRelationship_whenAuthenticated_andReceiverExists() {
        final var senderLogin = "test";
        final var senderId = 1;
        final var receiverLogin = "receiver";
        final var receiverId = 2;
        final var createdAt = LocalDateTime.now();

        when(authenticationService.isUserAnonymous()).thenReturn(false);
        when(authenticationService.getLoginName()).thenReturn(senderLogin);
        when(userRepository.findUserId(senderLogin)).thenReturn(Optional.of(senderId));
        when(userRepository.findUserId(receiverLogin)).thenReturn(Optional.of(receiverId));
        mockClock(createdAt);
        when(doFactory.createUserRelationship(senderId, receiverId, createdAt))
                .thenReturn(UserRelationshipTestUtils.prepareNewFriendRequest(senderId, receiverId, createdAt));

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
    public void throwsIllegalArgumentException_whenReceiverDoesNotExist() {
        final var receiverLogin = "throws";

        when(authenticationService.isUserAnonymous()).thenReturn(false);
        when(authenticationService.getLoginName()).thenReturn("logged in");
        when(userRepository.findUserId(receiverLogin)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userRelationshipService.createNewRelationship(receiverLogin),
                "Creating new relationship has to throw when receiver does not exist!");
    }

    @Test
    public void throwsIllegalArgument_whenSenderDoesNotExist() {
        final var senderLogin = "throws";
        final var receiverLogin = "receiver";

        when(authenticationService.isUserAnonymous()).thenReturn(false);
        when(authenticationService.getLoginName()).thenReturn(senderLogin);
        when(userRepository.findUserId(receiverLogin)).thenReturn(Optional.of(1));

        assertThrows(IllegalArgumentException.class, () -> userRelationshipService.createNewRelationship(receiverLogin),
                "Creating new relationship has to throw when receiver does not exist!");
    }

    @Test
    public void throwsIllegalArgument_whenReceiverIsSender() {
        final var senderLogin = "receiver";
        final var senderId = 56;
        final var createdAt = LocalDateTime.now();

        when(authenticationService.isUserAnonymous()).thenReturn(false);
        when(authenticationService.getLoginName()).thenReturn(senderLogin);
        when(userRepository.findUserId(senderLogin)).thenReturn(Optional.of(senderId));
        mockClock(createdAt);
        when(doFactory.createUserRelationship(senderId, senderId, createdAt))
                .thenReturn(UserRelationshipTestUtils.prepareNewFriendRequest(senderId, senderId, createdAt));

        assertThrows(IllegalArgumentException.class, () -> userRelationshipService.createNewRelationship(senderLogin),
                "Creating new relationship with self has to throw!");
    }

    private void mockClock(final LocalDateTime createdAt) {
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(createdAt.toInstant(ZoneOffset.UTC));
    }
}
