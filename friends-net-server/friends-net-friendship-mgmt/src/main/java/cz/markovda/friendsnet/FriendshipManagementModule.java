package cz.markovda.friendsnet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * @author <a href="mailto:">David Markov</a>
 * @since 29.12.21
 */
@Configuration
@ComponentScan(basePackages = "cz.markovda.friendsnet.friendship")
public class FriendshipManagementModule {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
