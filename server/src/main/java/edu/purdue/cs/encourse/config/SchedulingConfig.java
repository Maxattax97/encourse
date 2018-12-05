package edu.purdue.cs.encourse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Bean that sets up tasks to run automatically
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    @Bean
    public Scheduler scheduler(){
        return new Scheduler();
    }
}
