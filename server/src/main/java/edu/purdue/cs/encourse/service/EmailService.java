package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.User;

import javax.management.relation.InvalidRelationIdException;

public interface EmailService {
    String sendGeneratedPasswordMessage(Account account);
    void sendGeneratedReportMessage(Long accountID, String reportID) throws InvalidRelationIdException;
}
