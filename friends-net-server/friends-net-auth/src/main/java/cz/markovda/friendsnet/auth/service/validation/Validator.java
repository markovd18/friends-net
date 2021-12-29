package cz.markovda.friendsnet.auth.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 26.12.21
 */
@Component
@RequiredArgsConstructor
public class Validator {

    private final javax.validation.Validator validator;

    public void validate(Object object) {
        final Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
        if (!constraintViolations.isEmpty()) {
            throw new ValidationException(constraintViolations);
        }
    }
}
