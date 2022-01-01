package cz.markovda.friendsnet.friendship.service.impl;

import cz.markovda.friendsnet.auth.dos.IUserDO;
import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.friendsnet.auth.vos.IVOFactory;
import cz.markovda.friendsnet.friendship.service.IUserSearchService;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 30.12.21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserSearchService implements IUserSearchService {

    private final IUserRepository userRepository;
    private final IVOFactory authVoFactory;

    @Override
    public List<IUserVO> findUsersWithNamesContainingString(final String searchString) {
        log.debug("Start of findUsersWithNamesContainingString method (args: {})", searchString);
        Assert.notNull(searchString, "Search string may not be null!");
        final List<IUserDO> foundUsers = userRepository.findUsersWithNameLike(searchString);
        log.debug("End of findUsersWithNamesContainingString method.");
        return createUsersVO(foundUsers);
    }

    private List<IUserVO> createUsersVO(final List<IUserDO> userDOList) {
        return userDOList.stream()
                .map(this::createUserVO)
                .collect(Collectors.toList());
    }

    private IUserVO createUserVO(final IUserDO userDO) {
        return authVoFactory.createUser(userDO.getLogin(), null, userDO.getName());
    }
}
