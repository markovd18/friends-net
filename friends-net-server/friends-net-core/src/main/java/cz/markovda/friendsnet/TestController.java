package cz.markovda.friendsnet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 21.12.21
 */
@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "hello, docker container!";
    }
}
