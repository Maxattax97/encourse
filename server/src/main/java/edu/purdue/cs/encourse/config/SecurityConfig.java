package edu.purdue.cs.encourse.config;

import edu.purdue.cs.encourse.auth.*;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    TokenStore tokenStore () {
        return new InMemoryTokenStore();
    }

    @Bean
    Sender sender (JavaMailSender aJavaMailSender) {
        return new EmailSender(aJavaMailSender);
    }

    @Bean
    Authenticator authenticator () {
        return new SpringSecurityAuthenticator(tokenStore());
    }

    @Override
    protected void configure(HttpSecurity aHttpSecurity) throws Exception {
        aHttpSecurity.httpBasic()
                .disable()
                .authorizeRequests()
                .antMatchers("/css/**","/signin/**","/signup/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
                .permitAll();
    }
}
