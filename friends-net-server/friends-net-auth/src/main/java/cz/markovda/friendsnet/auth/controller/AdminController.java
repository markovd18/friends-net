package cz.markovda.friendsnet.auth.controller;

import cz.markovda.api.AdminControllerApi;
import cz.markovda.friendsnet.auth.service.IUserAuthService;
import cz.markovda.friendsnet.auth.service.role.EnumUserRoleAction;
import cz.markovda.friendsnet.auth.vos.IUserVO;
import cz.markovda.vo.EnumUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 20.01.22
 */
@RequiredArgsConstructor
@RestController
public class AdminController implements AdminControllerApi {

    private final IUserAuthService userAuthService;

    @Override
    public ResponseEntity<Void> addRoleToUser(final EnumUserRole role, final String username) {
        return runUserRoleAction(role, username, EnumUserRoleAction.ADD);
    }

    @Override
    public ResponseEntity<Void> removeRoleFromUser(final EnumUserRole role, final String username) {
        return runUserRoleAction(role, username, EnumUserRoleAction.REMOVE);
    }

    private ResponseEntity<Void> runUserRoleAction(final EnumUserRole role,
                                                   final String username,
                                                   final EnumUserRoleAction action) {
        try {
            userAuthService.changeUserRole(username, IUserVO.EnumUserRole.valueOf(role.name()), action);
            return ResponseEntity.ok(null);
        } catch (final AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (final IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
