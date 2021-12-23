package cz.markovda.friendsnet;

import cz.markovda.api.TestControllerApi;
import cz.markovda.vo.HelloResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 21.12.21
 */
@RestController
public class TestController implements TestControllerApi {

    @Override
    public ResponseEntity<HelloResponse> hello() {
        return ResponseEntity.ok(new HelloResponse().text("Hello, OpenAPI world!"));
    }
}
