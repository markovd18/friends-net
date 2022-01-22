package cz.markovda.friendsnet.auth.service.impl;

import cz.markovda.friendsnet.auth.dos.EnumUserRole;
import cz.markovda.friendsnet.auth.dos.projection.IUserRoleDO;
import cz.markovda.friendsnet.auth.dos.impl.UserDO;
import cz.markovda.friendsnet.auth.dos.impl.UserDOTestUtils;
import cz.markovda.friendsnet.auth.dos.impl.UserRoleDO;
import cz.markovda.friendsnet.auth.repository.IUserRepository;
import cz.markovda.friendsnet.auth.repository.IUserRoleRepository;
import cz.markovda.friendsnet.auth.service.IAuthenticationService;
import cz.markovda.friendsnet.auth.service.validation.impl.Validator;
import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.friendsnet.auth.vos.impl.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 25.12.21
 */
public class UserAuthServiceTest {

    private UserAuthServiceImpl userAuthService;
    private IUserRepository userRepository;
    private IUserRoleRepository userRoleRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void prepareTest() {
        userRepository = mock(IUserRepository.class);
        userRoleRepository = mock(IUserRoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userAuthService = new UserAuthServiceImpl(userRepository, userRoleRepository, passwordEncoder,
                mock(Validator.class), mock(IAuthenticationService.class));
    }

    @Test
    public void loadsExistingUserByUsername() {
        final var username = "username";

        when(userRepository.findByLoginFetchRoles(username)).thenReturn(Optional.of(UserDOTestUtils.prepareUserDO()));
        assertNotNull(userAuthService.loadUserByUsername("username"),
                "User loaded by user authentication service should not be null");
    }

    @Test
    public void throwsWhenUserDoesNotExist() {
        final var username = "not-existing-user";

        when(userRepository.findByLoginFetchRoles(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userAuthService.loadUserByUsername(username),
                "Searching for non-existing user should throw an exception!");
    }

    @Test
    public void loadsExistingUserByUsername_withCorrectVAttributes() {
        final var username = "little kid lover";

        final UserDO userDO = UserDOTestUtils.prepareUserDO(username);
        when(userRepository.findByLoginFetchRoles(username)).thenReturn(Optional.of(userDO));
        final UserDetails result = userAuthService.loadUserByUsername(username);
        assertAll(() -> assertEquals(userDO.getLogin(), result.getUsername(), "User Details username has to match username in DB!"),
                () -> assertEquals(userDO.getPassword(), result.getPassword(), "User Details password has to match password in DB!"),
                () -> assertEquals(userDO.getRoles().size(), result.getAuthorities().size(), "Number of User Details authorities has to match number of roles in DB!"),
                () -> assertIterableEquals(userDO.getRoles().stream()
                        .map(UserRoleDO::getName)
                        .map(Enum::name)
                        .map(n -> "ROLE_" + n)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet()), result.getAuthorities(),
                        "User Details authorities have to contain roles from DB!"));
    }

    @Test
    public void throwsWhenRegisteredUserExists() {
        final IUserVO userVO = new UserVO("great", "stuff", "Nameless", Set.of(IUserVO.EnumUserRole.ADMIN));

        when(userRepository.existsByLogin(userVO.getLogin())).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> userAuthService.createNewUser(userVO),
                "Creating existing user should throw an exception!");
    }

    @Test
    public void throwsWhenCreatedUserHasIdZero() {
        final UserDO userDO = UserDOTestUtils.prepareUserDO(0);
        final IUserVO userVO = new UserVO("another", "stuff", "Not nameless", Set.of(IUserVO.EnumUserRole.ADMIN));

        when(userRepository.save(userDO)).thenReturn(userDO);
        assertThrows(RuntimeException.class, () -> userAuthService.createNewUser(userVO));
    }

    @Test
    public void findsExistingUser() {
        final var username = "really existing";

        final UserDO userDO = UserDOTestUtils.prepareUserDO(username);
        when(userRepository.findByLoginFetchRoles(username)).thenReturn(Optional.of(userDO));
        final IUserVO userVO = userAuthService.findUserByUsername(username);
        assertAll(() -> assertEquals(userDO.getLogin(), userVO.getLogin(), "Login of returned user has to match the login of DB record!"),
                () -> assertNull(userVO.getPassword(), "Password of the user should not be returned!"),
                () -> assertEquals(userDO.getName(), userVO.getName(), "Name of returned user has to match the name of DB record!"),
                () -> assertIterableEquals(userDO.getRoles().stream()
                                .map(IUserRoleDO::getName)
                                .map(Enum::name)
                                .map(IUserVO.EnumUserRole::valueOf)
                                .collect(Collectors.toSet()), userVO.getRoles(),
                        "Role of returned user has to match the role of DB record!"));
    }

    @Test
    public void returnsNull_whenUserDoesNotExist() {
        final var username = "I do not exist";

        when(userRepository.findByLoginFetchRoles(username)).thenReturn(Optional.empty());
        assertNull(userAuthService.findUserByUsername(username), "When user does not exist, service should return NULL!");
    }

}
