package cz.markovda.friendsnet.controller;

import cz.markovda.api.AuthenticationControllerApi;
import cz.markovda.friendsnet.config.jwt.JwtUtils;
import cz.markovda.friendsnet.service.IUserAuthService;
import cz.markovda.vo.JwtVO;
import cz.markovda.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController implements AuthenticationControllerApi {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IUserAuthService userAuthService;

    @Override
    public ResponseEntity<JwtVO> login(final LoginVO loginVO) {
        final UserDetails userDetails;
        try {
            userDetails = userAuthService.loadUserByUsername(loginVO.getLogin());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }

        authenticate(loginVO.getLogin(), loginVO.getPassword());
        final String token = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtVO().token(token));
    }

    record JwtResponse(String token) {}

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
