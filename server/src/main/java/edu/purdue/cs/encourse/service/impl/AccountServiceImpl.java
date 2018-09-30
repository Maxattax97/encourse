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


    public Account retrieveAccount(@NonNull String userName) {
        return accountRepository.findByUserName(userName);
    }

    public Account retrieveAccountByID(@NonNull String userID) {
        return accountRepository.findByUserID(userID);
    }

    public Student retrieveStudent(@NonNull String userName) {
        return studentRepository.findByUserName(userName);
    }

    public TeachingAssistant retrieveTA(@NonNull String userName) {
        return teachingAssistantRepository.findByUserName(userName);
    }

    public Professor retrieveProfessor(@NonNull String userName) {
        return professorRepository.findByUserName(userName);
    }

    public CollegeAdmin retrieveAdmin(@NonNull String userName) {
        return adminRepository.findByUserName(userName);
    }

    public List<Account> retrieveAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        if(accounts.isEmpty()) {
            return null;
        }
        return accounts;
    }
}
