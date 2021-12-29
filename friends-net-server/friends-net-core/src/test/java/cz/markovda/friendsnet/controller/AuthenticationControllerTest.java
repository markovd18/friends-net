package cz.markovda.friendsnet.controller;

import cz.markovda.friendsnet.config.jwt.JwtUtils;
import cz.markovda.friendsnet.service.IUserAuthService;
import cz.markovda.friendsnet.service.validation.ValidationException;
import cz.markovda.friendsnet.vos.IUserVO;
import cz.markovda.friendsnet.vos.IVOFactory;
import cz.markovda.friendsnet.vos.impl.UserVO;
import cz.markovda.vo.UserAuthenticationVO;
import cz.markovda.vo.UserCredentialsVO;
import cz.markovda.vo.UserRegistrationDataVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 27.12.21
 */
public class AuthenticationControllerTest {

    private AuthenticationController authenticationController;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private IUserAuthService userAuthService;
    private IVOFactory voFactory;

    @BeforeEach
    public void prepareTest() {
        userAuthService = mock(IUserAuthService.class);
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtils = mock(JwtUtils.class);
        voFactory = mock(IVOFactory.class);

        authenticationController = new AuthenticationController(authenticationManager, jwtUtils, userAuthService, voFactory);
    }

    @Test
    public void returnsToken_whenExistingUserLogsIn() {
        final UserCredentialsVO userCredentialsVO = new UserCredentialsVO()
                .login("existing")
                .password("password");
        final IUserVO userDetails = new UserVO(userCredentialsVO.getLogin(), null, "name", IUserVO.EnumUserRole.USER);
        final String generatedToken = "test-token";
        when(userAuthService.findUserByUsername(userCredentialsVO.getLogin())).thenReturn(userDetails);
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails.getLogin(), userDetails.getPassword())))
                .thenReturn(null);
        when(jwtUtils.generateToken(userDetails)).thenReturn(generatedToken);

        final ResponseEntity<UserAuthenticationVO> response = authenticationController.login(userCredentialsVO);
        assertAll(() -> assertNotNull(response, "Controller has to respond with non-null object!"),
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Response to existing user login has to be OK!"),
                () -> assertNotNull(response.getBody(), "Response body may not be null!"),
                () -> assertEquals(generatedToken, Objects.requireNonNull(response.getBody()).getToken(),
                        "Returned token has to be equal to the generated one!"));
    }

    @Test
    public void returnsBadRequest_whenUserDoesNotExist() {
        final UserCredentialsVO userCredentialsVO = new UserCredentialsVO()
                .login("not_existing")
                .password("great-password");
        when(userAuthService.loadUserByUsername(userCredentialsVO.getLogin()))
                .thenThrow(new UsernameNotFoundException(""));

        assertEquals(HttpStatus.BAD_REQUEST, authenticationController.login(userCredentialsVO).getStatusCode(),
                "Controller has to return BAD REQUEST when non-existent user logs in!");
    }

    @Test
    public void returnsOk_whenValidUserRegisters() {
        final UserRegistrationDataVO userCredentialsVO = new UserRegistrationDataVO()
                .login("new-user")
                .password("badpswd")
                .name("New User");
        IUserVO userVO = new UserVO(userCredentialsVO.getLogin(), userCredentialsVO.getPassword(), userCredentialsVO.getName(), null);
        when(voFactory.createUser(userCredentialsVO.getLogin(), userCredentialsVO.getPassword(), userCredentialsVO.getName()))
                .thenReturn(userVO);
        when(userAuthService.createNewUser(userVO)).thenReturn(userVO);

        assertEquals(HttpStatus.OK, authenticationController.register(userCredentialsVO).getStatusCode(),
                "Controller has to return OK when registering valid user!");
    }

    @Test
    public void returnsBadRequest_whenUserAuthServiceThrows() {
        final UserRegistrationDataVO userCredentialsVO = new UserRegistrationDataVO()
                .login("bad?username")
                .password("123456")
                .name("Bad Name Too");
        IUserVO userVO = new UserVO(userCredentialsVO.getLogin(), userCredentialsVO.getPassword(), userCredentialsVO.getName(), null);
        when(voFactory.createUser(userCredentialsVO.getLogin(), userCredentialsVO.getPassword(), userCredentialsVO.getName()))
                .thenReturn(userVO);
        when(userAuthService.createNewUser(userVO)).thenThrow(new ValidationException(null));
        assertEquals(HttpStatus.BAD_REQUEST, authenticationController.register(userCredentialsVO).getStatusCode(),
                "Controller has to return BAD_REQUEST when authentication service throws");
    }
}
