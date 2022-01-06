package cz.markovda.friendsnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 29.12.21
 */
@SpringBootApplication
@EnableJpaRepositories
public class AuthApplicationTest {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplicationTest.class, args);
    }

}
