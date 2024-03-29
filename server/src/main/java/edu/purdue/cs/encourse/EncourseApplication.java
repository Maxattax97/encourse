package edu.purdue.cs.encourse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class, JmxAutoConfiguration.class })
public class EncourseApplication {
    public static void main(String[] args) {
        SpringApplication.run(EncourseApplication.class, args);
    }
}
