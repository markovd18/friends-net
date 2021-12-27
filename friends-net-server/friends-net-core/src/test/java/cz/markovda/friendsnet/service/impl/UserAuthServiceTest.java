package cz.markovda.friendsnet.service.impl;

import cz.markovda.friendsnet.dos.IDOFactory;
import cz.markovda.friendsnet.dos.IUserDO;
import cz.markovda.friendsnet.repository.IUserRepository;
import cz.markovda.friendsnet.service.validation.Validator;
import cz.markovda.friendsnet.utils.UserDOTestUtils;
import cz.markovda.friendsnet.vos.IUserVO;
import cz.markovda.friendsnet.vos.impl.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private IDOFactory doFactory;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void prepareTest() {
        userRepository = mock(IUserRepository.class);
        doFactory = mock(IDOFactory.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userAuthService = new UserAuthServiceImpl(userRepository, doFactory, passwordEncoder, mock(Validator.class));
    }

    @Test
    public void loadsExistingUserByUsername() {
        final var username = "username";

        when(userRepository.findUserWithRoleByLogin(username)).thenReturn(Optional.of(UserDOTestUtils.prepareUser()));
        assertNotNull(userAuthService.loadUserByUsername("username"),
                "User loaded by user authentication service should not be null");
    }

    @Test
    public void throwsWhenUserDoesNotExist() {
        final var username = "not-existing-user";

        when(userRepository.findUserWithRoleByLogin(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userAuthService.loadUserByUsername(username),
                "Searching for non-existing user should throw an exception!");
    }

    @Test
    public void loadsExistingUserByUsername_withCorrectVAttributes() {
        final var username = "little kid lover";

        final IUserDO userDO = UserDOTestUtils.prepareUser(username);
        when(userRepository.findUserWithRoleByLogin(username)).thenReturn(Optional.of(userDO));
        final UserDetails result = userAuthService.loadUserByUsername(username);
        assertAll(() -> assertEquals(userDO.getLogin(), result.getUsername(), "User Details username has to match username in DB!"),
                () -> assertEquals(userDO.getPassword(), result.getPassword(), "User Details password has to match password in DB!"),
                () -> assertEquals(1, result.getAuthorities().size(), "Number of User Details authorities has to match number of roles in DB!"),
                () -> assertNotNull(result.getAuthorities().stream()
                        .filter(role -> role.getAuthority().equals("ROLE_" + userDO.getRole().name()))
                        .findFirst()
                        .orElse(null), "User Details authorities have to contain roles from DB!"));
    }

    @Test
    public void registersNewValidUser() {
        final IUserVO userVO = new UserVO("login", "password", "Sir John", IUserVO.EnumUserRole.USER);
        final String encodedPassword = "super-encoded-password";
        final IUserDO userToSave = UserDOTestUtils.prepareUser(0, userVO.getLogin(), encodedPassword, IUserDO.EnumUserRole.USER);

        when(userRepository.userWithLoginExists(userVO.getLogin())).thenReturn(false);
        when(userRepository.saveUser(userToSave)).thenReturn(1);
        when(passwordEncoder.encode(userVO.getPassword())).thenReturn(encodedPassword);
        when(doFactory.createUser(userVO.getLogin(), encodedPassword, userVO.getName())).thenReturn(userToSave);

        final IUserVO createdUser = userAuthService.createNewUser(userVO);
        assertAll(() -> assertNotNull(createdUser, "Created user may not be NULL!"),
                () -> assertEquals(userVO.getLogin(), createdUser.getLogin(), "Login of created user has to be equal to the argument!"),
                () -> assertEquals(encodedPassword, createdUser.getPassword(), "Password of created user has to be equal to the argument!"),
                () -> assertEquals(IUserVO.EnumUserRole.USER, createdUser.getRole(), "Created user has to have role USER!"));
    }

    @Test
    public void throwsWhenRegisteredUserExists() {
        final IUserVO userVO = new UserVO("great", "stuff", "Nameless", IUserVO.EnumUserRole.ADMIN);

        when(userRepository.userWithLoginExists(userVO.getLogin())).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> userAuthService.createNewUser(userVO),
                "Creating existing user should throw an exception!");
    }

    @Test
    public void throwsWhenCreatedUserHasIdZero() {
        final IUserDO userDO = UserDOTestUtils.prepareUser();
        final IUserVO userVO = new UserVO("another", "stuff", "Not nameless", IUserVO.EnumUserRole.ADMIN);

        when(userRepository.saveUser(userDO)).thenReturn(0);
        assertThrows(RuntimeException.class, () -> userAuthService.createNewUser(userVO));
    }

}
