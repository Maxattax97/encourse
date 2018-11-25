package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.service.AccountService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contains implementations for all services pertaining to account operations.
 * Primarily used for logging in with an account.
 *
 * @author William Jordan Reed
 * @author reed226@purdue.edu
 */
@Service(value = AccountServiceImpl.NAME)
public class AccountServiceImpl implements AccountService {

    public final static String NAME = "AccountService";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeachingAssistantRepository teachingAssistantRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AdminRepository adminRepository;

    /**
     * The primary method for retrieving an account from database
     *
     * @param userName Front-end identifier used for the account
     * @return Account matching the specified userName
     */
    public Account retrieveAccount(@NonNull String userName) {
        return accountRepository.findByUserName(userName);
    }

    /**
     * A secondary method for retrieving an account
     * Only useful for backend code, frontend does not have access to userIDs
     *
     * @param userID Back-end identifier used for the account
     * @return Account matching the specified userID
     */
    public Account retrieveAccountByID(@NonNull String userID) {
        return accountRepository.findByUserID(userID);
    }

    /**
     * Retrieves an account as a student
     *
     * @param userName Front-end identifier used for the account
     * @return Student account matching the specified userName
     */
    public Student retrieveStudent(@NonNull String userName) {
        return studentRepository.findByUserName(userName);
    }

    /**
     * Retrieves an account as a teaching assistant
     *
     * @param userName Front-end identifier used for the account
     * @return Teaching assistant account matching the specified userName
     */
    public TeachingAssistant retrieveTA(@NonNull String userName) {
        return teachingAssistantRepository.findByUserName(userName);
    }

    /**
     * Retrieves an account as a professor
     *
     * @param userName Front-end identifier used for the account
     * @return Professor account matching the specified userName
     */
    public Professor retrieveProfessor(@NonNull String userName) {
        return professorRepository.findByUserName(userName);
    }

    /**
     * Retrieves an account as an administrator
     *
     * @param userName Front-end identifier used for the account
     * @return Administrator account matching the specified userName
     */
    public CollegeAdmin retrieveAdmin(@NonNull String userName) {
        return adminRepository.findByUserName(userName);
    }

    /**
     * Retrieves all accounts currently in the database
     * Used by administrators to see and manage accounts
     *
     * @return All accounts in the database
     */
    public List<Account> retrieveAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        if(accounts.isEmpty()) {
            return null;
        }
        return accounts;
    }
}
