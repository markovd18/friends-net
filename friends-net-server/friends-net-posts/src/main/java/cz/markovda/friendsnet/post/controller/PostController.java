package cz.markovda.friendsnet.post.controller;

import cz.markovda.api.PostControllerApi;
import cz.markovda.friendsnet.auth.service.validation.impl.ValidationException;
import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.friendsnet.post.service.IPostService;
import cz.markovda.friendsnet.post.vos.INewPostVO;
import cz.markovda.friendsnet.post.vos.IPostVO;
import cz.markovda.friendsnet.post.vos.IVOFactory;
import cz.markovda.vo.NewPostDataVO;
import cz.markovda.vo.PostVO;
import cz.markovda.vo.UserIdentificationDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 07.01.22
 */
@RestController
@RequiredArgsConstructor
public class PostController implements PostControllerApi {

    private final IPostService postService;
    private final IVOFactory voFactory;

    @Override
    public ResponseEntity<Void> createNewPost(final NewPostDataVO newPostDataVO) {
        try {
            postService.createNewPost(createNewPostVO(newPostDataVO));
            return ResponseEntity.ok(null);
        } catch (final ValidationException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (final AccessDeniedException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<List<PostVO>> findNewestPosts(final Integer maxPosts,
                                                  final LocalDateTime newerThan) {

        try {
            final List<IPostVO> foundPosts = postService.findNewestPosts(maxPosts, newerThan);
            return ResponseEntity.ok(createPostsApiVO(foundPosts));
        } catch (final IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (final AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    private INewPostVO createNewPostVO(final NewPostDataVO newPostDataVO) {
        return voFactory.createNewPostVO(
                newPostDataVO.getTitle(),
                newPostDataVO.getContent(),
                newPostDataVO.getDateCreated(),
                newPostDataVO.getIsAnnouncement());
    }

    private List<PostVO> createPostsApiVO(final List<IPostVO> foundPosts) {
        return foundPosts.stream()
                .map(this::createPostApiVO)
                .collect(Collectors.toList());
    }

    private PostVO createPostApiVO(final IPostVO postVO) {
        final UserIdentificationDataVO author = createUserApiVO(postVO.getAuthor());
        return new PostVO()
                .author(author)
                .title(postVO.getTitle())
                .content(postVO.getContent())
                .dateCreated(postVO.getDateCreated())
                .isAnnouncement(postVO.isAnnouncement());
    }

    private UserIdentificationDataVO createUserApiVO(final IUserVO author) {
        return new UserIdentificationDataVO()
                .login(author.getLogin())
                .name(author.getName());
    }
}
