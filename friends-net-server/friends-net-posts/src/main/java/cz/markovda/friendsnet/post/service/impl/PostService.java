package cz.markovda.friendsnet.post.service.impl;

import cz.markovda.friendsnet.auth.dos.impl.UserDO;
import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.service.IAuthenticationService;
import cz.markovda.friendsnet.auth.service.validation.IValidator;
import cz.markovda.friendsnet.auth.service.validation.impl.ValidationException;
import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.friendsnet.auth.vos.impl.UserVO;
import cz.markovda.friendsnet.post.dos.PostDO;
import cz.markovda.friendsnet.post.dos.projection.INewestPostDO;
import cz.markovda.friendsnet.post.repository.IPostRepository;
import cz.markovda.friendsnet.post.service.IPostService;
import cz.markovda.friendsnet.post.vos.INewPostVO;
import cz.markovda.friendsnet.post.vos.IPostVO;
import cz.markovda.friendsnet.post.vos.impl.PostVO;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    @Transactional(readOnly = true)
    public List<IPostVO> findNewestPosts(final Integer max, final LocalDateTime newerThan) {
        log.debug("Start of findNewestPosts method (args: {}, {})", max, newerThan);
        final String login = getAuthenticatedUserLogin();

        validatePostQueryParameters(max, newerThan);

        final Integer limit = createPostLimit(max);
        final LocalDateTime dateLimit = createCreationDateLimit(newerThan);

        final Page<INewestPostDO> foundPosts = postRepository.findNewestPosts(dateLimit, login, PageRequest.ofSize(limit));
        final List<IPostVO> result = createPostsVO(foundPosts);

        log.debug("End of findNewestPosts method. Found {} results", result.size());
        return result;
    }

    private LocalDateTime createCreationDateLimit(final LocalDateTime newerThan) {
        return newerThan == null ? LocalDateTime.now().minusWeeks(1) : newerThan;
    }

    private Integer createPostLimit(final Integer max) {
        return max == null ? 20 : max;
    }

    private String getAuthenticatedUserLogin() {
        if (authenticationService.isUserAnonymous()) {
            throw new AccessDeniedException("Only authenticated users may query posts");
        }

        return authenticationService.getLoginName();
    }

    private List<IPostVO> createPostsVO(final Page<INewestPostDO> foundPosts) {
        return foundPosts.stream()
                .map(this::createPostVO)
                .collect(Collectors.toList());
    }

    private IPostVO createPostVO(final INewestPostDO foundPost) {
        final IUserVO authorVO = new UserVO(foundPost.getAuthorLogin(), null, foundPost.getAuthorName(), null);
        return new PostVO(authorVO, foundPost.getTitle(), foundPost.getContent(), foundPost.getDateCreated(), foundPost.isAnnouncement());
    }

    private void validatePostQueryParameters(final Integer max, final LocalDateTime newerThan) {
        if (max != null && max < 5) {
            throw new IllegalArgumentException("Maximum number of posts has to be at least 5, got " + max);
        }

        if (newerThan != null && !newerThan.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Borderline date time has to be in the past");
        }
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
