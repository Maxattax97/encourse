package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.service.AccountService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * @param userName of account being retrieved
     * @return account associated with userName
     */
    public Account retrieveAccount(@NonNull String userName) {
        return accountRepository.findByUserName(userName);
    }

    /**
     * A secondary method for retrieving an account
     * Only useful for backend code, frontend does not have access to userIDs
     *
     * @param userID of account being retrieved
     * @return account associated with userID
     */
    public Account retrieveAccountByID(@NonNull String userID) {
        return accountRepository.findByUserID(userID);
    }

    /**
     * Retrieves an account as a student
     *
     * @param userName of account being retrieved
     * @return student account associated with userName
     */
    public Student retrieveStudent(@NonNull String userName) {
        return studentRepository.findByUserName(userName);
    }

    /**
     * Retrieves an account as a student
     *
     * @param userName of account being retrieved
     * @return teaching assistant account associated with userName
     */
    public TeachingAssistant retrieveTA(@NonNull String userName) {
        return teachingAssistantRepository.findByUserName(userName);
    }

    /**
     * Retrieves an account as a student
     *
     * @param userName of account being retrieved
     * @return professor account associated with userName
     */
    public Professor retrieveProfessor(@NonNull String userName) {
        return professorRepository.findByUserName(userName);
    }

    /**
     * Retrieves an account as a student
     *
     * @param userName of account being retrieved
     * @return administrator account associated with userName
     */
    public CollegeAdmin retrieveAdmin(@NonNull String userName) {
        return adminRepository.findByUserName(userName);
    }

    /**
     * Retrieves all accounts currently in the database
     * Used by administrators to see and manage accounts
     *
     * @return all accounts in the database
     */
    public List<Account> retrieveAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        if(accounts.isEmpty()) {
            return null;
        }
        return accounts;
    }
}
