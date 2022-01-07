package cz.markovda.friendsnet.post.controller;

import cz.markovda.api.PostControllerApi;
import cz.markovda.friendsnet.auth.service.validation.impl.ValidationException;
import cz.markovda.friendsnet.post.service.IPostService;
import cz.markovda.friendsnet.post.vos.INewPostVO;
import cz.markovda.friendsnet.post.vos.IVOFactory;
import cz.markovda.vo.NewPostDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RestController;

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

    private INewPostVO createNewPostVO(final NewPostDataVO newPostDataVO) {
        return voFactory.createNewPostVO(
                newPostDataVO.getTitle(),
                newPostDataVO.getContent(),
                newPostDataVO.getDateCreated().atStartOfDay(),
                newPostDataVO.getIsAnnouncement());
    }
}
