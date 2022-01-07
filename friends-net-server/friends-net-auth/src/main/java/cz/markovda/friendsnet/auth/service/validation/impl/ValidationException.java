package cz.markovda.friendsnet.auth.service.validation.impl;

import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
@RequiredArgsConstructor
public class ValidationException extends RuntimeException {

    private final Set<ConstraintViolation<Object>> constraintViolations;

    @Override
    public String getMessage() {
        final var sb = new StringBuilder();
        for (ConstraintViolation<?> violation : constraintViolations) {
            sb.append(violation.getMessage())
                    .append(". ");
        }

        return sb.toString();
    }
}
