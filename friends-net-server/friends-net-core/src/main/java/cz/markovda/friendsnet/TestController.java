package cz.markovda.friendsnet;

import cz.markovda.api.TestControllerApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 21.12.21
 */
@RestController
public class TestController implements TestControllerApi {

    @Override
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello, Open API world of docker!");
    }
}
