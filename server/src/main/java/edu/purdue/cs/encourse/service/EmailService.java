package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.User;

public interface EmailService {
    String sendGeneratedPasswordMessage(Account account);
}
