package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.service.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.management.relation.InvalidRelationIdException;
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

    @Autowired
    private ReportService reportService;
    
    @Autowired
    private AccountService accountService;

    @Value("${spring.mail.username}")
    private String from;

    public String sendGeneratedPasswordMessage(Account account) {
        String password = generatePassword(20);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(account.getEduEmail());
        message.setSubject("EnCourse Password");
        message.setText("Please use the following password to login to EnCourse: \n\n" + password);
        emailSender.send(message);
        return userPasswordEncoder.encode(password);
    }

    public void sendGeneratedReportMessage(Long accountID, String reportID) throws InvalidRelationIdException {
        String lock = reportService.shareReport(reportID);
        String link = "vm2.cs.purdue.edu/api/report?reportID=" + reportID + "&lock=" + lock;

        Account account = accountService.getAccount(accountID);
        if (account == null) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(account.getEduEmail());
        message.setSubject("EnCourse Dishonesty Report");
        message.setText("Please use the following link to obtain the dishonesty report: \n\n" + link);
        emailSender.send(message);
    }

    private String generatePassword(int size) {
        char[] possibleCharacters = (new String("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?")).toCharArray();
        return RandomStringUtils.random( size, 0, possibleCharacters.length - 1, false, false, possibleCharacters, new SecureRandom());
    }

}
