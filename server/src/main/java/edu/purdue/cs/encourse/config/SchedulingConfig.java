package edu.purdue.cs.encourse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    @Bean
    public Scheduler scheduler(){
        return new Scheduler();
    }
}
