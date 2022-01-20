package cz.markovda.friendsnet.post.service.impl;

import cz.markovda.friendsnet.auth.dos.impl.UserDO;
import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.service.IAuthenticationService;
import cz.markovda.friendsnet.auth.service.validation.IValidator;
import cz.markovda.friendsnet.auth.service.validation.impl.ValidationException;
import cz.markovda.friendsnet.post.repository.IPostRepository;
import cz.markovda.friendsnet.post.vos.impl.NewPostVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
public class PostServiceTest {

    private PostService postService;

    private IValidator validator;
    private IPostRepository postRepository;
    private IUserRepository userRepository;
    private IAuthenticationService authenticationService;

    @BeforeEach
    public void prepareTest() {
        validator = mock(IValidator.class);
        postRepository = mock(IPostRepository.class);
        userRepository = mock(IUserRepository.class);
        authenticationService = mock(IAuthenticationService.class);

        postService = new PostService(validator, postRepository, userRepository, authenticationService);
    }

    @Test
    public void createsNewValidPost_whenAuthenticated() {
        final var login = "user";
        final var post = new NewPostVO("Content", LocalDateTime.now(), false);

        when(authenticationService.isUserAnonymous()).thenReturn(false);
        when(authenticationService.isUserAdmin()).thenReturn(false);
        when(authenticationService.getLoginName()).thenReturn(login);
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(new UserDO(login, "password", "User")));

        assertDoesNotThrow(() -> postService.createNewPost(post), "Valid post should be created");
    }

    @Test
    public void throwsWhenVOIsNull() {
        assertThrows(IllegalArgumentException.class, () -> postService.createNewPost(null),
                "Creating post with null parameter must throw");
    }

    @Test
    public void throwsWhenVONotValid() {
        final var post = new NewPostVO("T", null, LocalDateTime.now(), false);
        doThrow(ValidationException.class).when(validator).validate(post);

        when(authenticationService.isUserAdmin()).thenReturn(false);
        when(authenticationService.isUserAnonymous()).thenReturn(false);

        assertThrows(ValidationException.class, () -> postService.createNewPost(post),
                "Invalid input has to end up in throwing");
    }

    @Test
    public void throwsAccessDenied_whenNotAuthenticated() {
        final var post = new NewPostVO("T", null, LocalDateTime.now(), false);
        when(authenticationService.isUserAnonymous()).thenReturn(true);
        when(authenticationService.isUserAdmin()).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> postService.createNewPost(post),
                "Creating post unauthenticated has to throw");
    }

    @Test
    public void throwsAccessDenied_whenPostIsAnnouncement_andUserIsNotAdmin() {
        final var post = new NewPostVO("Announcement", "Important", LocalDateTime.now(), true);
        when(authenticationService.isUserAnonymous()).thenReturn(false);
        when(authenticationService.isUserAdmin()).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> postService.createNewPost(post),
                "Creating announcement as regular user has to throw");
    }

    @Test
    public void throwsInvalidState_whenUserDoesNotExist() {
        final var login = "admin";
        final var post = new NewPostVO("Announcement", "Important", LocalDateTime.now(), true);
        when(authenticationService.isUserAnonymous()).thenReturn(false);
        when(authenticationService.isUserAdmin()).thenReturn(true);
        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> postService.createNewPost(post),
                "When user not found, an exception has to be thrown");
    }
}
