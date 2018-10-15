package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.AccountRepository;
import edu.purdue.cs.encourse.database.UserRepository;
import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.service.EmailService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder userPasswordEncoder;

    @Value("${spring.mail.username}")
    private String from;

    public String sendGeneratedPasswordMessage(Account account) {
        String password = generatePassword();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(account.getEduEmail());
        message.setSubject("EnCourse Password");
        message.setText("Please use the following password to login to EnCourse: \n\n" + password);
        emailSender.send(message);
        return userPasswordEncoder.encode(password);
    }

    private String generatePassword() {
        char[] possibleCharacters = (new String("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?")).toCharArray();
        return RandomStringUtils.random( 15, 0, possibleCharacters.length - 1, false, false, possibleCharacters, new SecureRandom());
    }

}
