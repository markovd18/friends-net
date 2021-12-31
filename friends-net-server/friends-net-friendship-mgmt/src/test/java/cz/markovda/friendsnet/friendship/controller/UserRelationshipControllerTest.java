package cz.markovda.friendsnet.friendship.controller;

import cz.markovda.friendsnet.friendship.service.IUserRelationshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 31.12.21
 */
public class UserRelationshipControllerTest {

    private UserRelationshipController userRelationshipController;
    private IUserRelationshipService userRelationshipService;

    @BeforeEach
    public void prepareTest() {
        userRelationshipService = mock(IUserRelationshipService.class);
        userRelationshipController = new UserRelationshipController(userRelationshipService);
    }

    @Test
    public void returnsOk_whenReceiverExists_andIsAuthenticated() {
        final var receiverLogin = "John";

        assertEquals(ResponseEntity.ok(null), userRelationshipController.createFriendRequest(receiverLogin),
                "Controller has to return OK when successfully created friend request!");
    }

    @Test
    public void returnsBadRequest_whenReceiverDoesNotExist_andIsAuthenticated() {
        final var receiverLogin = "none";

        doThrow(new IllegalArgumentException()).when(userRelationshipService).createNewRelationship(receiverLogin);

        assertEquals(ResponseEntity.badRequest().build(), userRelationshipController.createFriendRequest(receiverLogin),
                "Controller has to return BAD REQUEST when receiver does not exist!");
    }

    @Test
    public void returns401_whenUnauthenticated() {
        final var receiverLogin = "Anonymous";

        doThrow(new AccessDeniedException("")).when(userRelationshipService).createNewRelationship(receiverLogin);

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
                userRelationshipController.createFriendRequest(receiverLogin),
                "Controller has to return UNAUTHORIZED when sender is not authenticated!");
    }
}
