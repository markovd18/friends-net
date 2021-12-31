package cz.markovda.friendsnet.friendship.controller;

import cz.markovda.api.UserRelationshipControllerApi;
import cz.markovda.friendsnet.friendship.service.IUserRelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 31.12.21
 */
@RestController
@RequiredArgsConstructor
public class UserRelationshipController implements UserRelationshipControllerApi {

    private final IUserRelationshipService userRelationshipService;

    @Override
    public ResponseEntity<Void> createFriendRequest(final String username) {
        try {
            userRelationshipService.createNewRelationship(username);
            return ResponseEntity.ok(null);
        } catch (final IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (final AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
