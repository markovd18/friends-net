package cz.markovda.friendsnet.friendship.service;

import cz.markovda.friendsnet.auth.vos.IUserVO;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
public interface IUserSearchService {

    List<IUserVO> findUsersWithNamesContainingString(@NotNull String searchString);
}
