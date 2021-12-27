package cz.markovda.friendsnet.controller;

import cz.markovda.api.AuthenticationControllerApi;
import cz.markovda.friendsnet.config.jwt.JwtUtils;
import cz.markovda.friendsnet.service.IUserAuthService;
import cz.markovda.friendsnet.service.validation.ValidationException;
import cz.markovda.friendsnet.vos.IUserVO;
import cz.markovda.friendsnet.vos.IVOFactory;
import cz.markovda.vo.JwtVO;
import cz.markovda.vo.UserCredentialsVO;
import cz.markovda.vo.UserRegistrationDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
@RestController
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationControllerApi {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IUserAuthService userAuthService;
    private final IVOFactory voFactory;

    @Override
    public ResponseEntity<JwtVO> login(final UserCredentialsVO credentialsVO) {
        final UserDetails userDetails;
        try {
            userDetails = userAuthService.loadUserByUsername(credentialsVO.getLogin());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }

        authenticate(credentialsVO.getLogin(), credentialsVO.getPassword());
        final String token = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtVO().token(token));
    }

    @Override
    public ResponseEntity<Void> register(final UserRegistrationDataVO registrationDataVO) {
        final IUserVO userToCreate = voFactory.createUser(registrationDataVO.getLogin(), registrationDataVO.getPassword(),
                registrationDataVO.getName());
        try {
            userAuthService.createNewUser(userToCreate);
        } catch (final IllegalStateException | ValidationException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(null);
    }


    private void authenticate(final String login, final String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        } catch (DisabledException e) {
            throw new RuntimeException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("INVALID_CREDENTIALS", e);
        }
    }
}
