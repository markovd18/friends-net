package cz.markovda.friendsnet.post.service.impl;

import cz.markovda.friendsnet.auth.dos.impl.UserDO;
import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.service.IAuthenticationService;
import cz.markovda.friendsnet.auth.service.validation.IValidator;
import cz.markovda.friendsnet.post.dos.PostDO;
import cz.markovda.friendsnet.post.repository.IPostRepository;
import cz.markovda.friendsnet.post.service.IPostService;
import cz.markovda.friendsnet.post.vos.INewPostVO;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostService implements IPostService {

    private final IValidator validator;
    private final IPostRepository postRepository;
    private final IUserRepository userRepository;
    private final IAuthenticationService authenticationService;

    @Transactional
    @Override
    public void createNewPost(@NotNull @Valid final INewPostVO post) {
        log.debug("Start of createNewPost method (args: {}).", post);
        validateNewPost(post);

        final UserDO author = getAuthenticatedUser();
        final PostDO postToSave = new PostDO(author, post.getTitle(), post.getContent(), post.isAnnouncement(), post.getDateCreated());
        postRepository.save(postToSave);
        log.debug("End of createNewPost method.");
    }

    private void validateNewPost(final INewPostVO post) {
        Assert.notNull(post, "Post may not be null");
        validator.validate(post);
        checkRightsToCreatePost(post);
    }

    private UserDO getAuthenticatedUser() {
        final String login = authenticationService.getLoginName();
        return getUserByLogin(login);
    }

    private UserDO getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    private void checkRightsToCreatePost(final INewPostVO post) {
        if (authenticationService.isUserAnonymous()) {
            throw new AccessDeniedException("Unauthenticated users may not create posts");
        }
        checkAnnouncementRights(post);
    }

    private void checkAnnouncementRights(final INewPostVO post) {
        if (post.isAnnouncement() && !authenticationService.isUserAdmin()) {
            throw new AccessDeniedException("Only admin may create an announcement post");
        }
    }
}
