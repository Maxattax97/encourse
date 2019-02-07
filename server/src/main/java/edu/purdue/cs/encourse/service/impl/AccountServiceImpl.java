package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.*;
import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.model.AccountModel;
import edu.purdue.cs.encourse.service.AccountService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.InvalidRelationIdException;
import javax.management.relation.RelationException;
import java.util.Optional;
import java.util.regex.Pattern;

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
    
    private final Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.edu$", Pattern.CASE_INSENSITIVE);
    
    private final AccountRepository accountRepository;
    
    private final StudentRepository studentRepository;
    
    private final ProfessorRepository professorRepository;
    
    private final AdminRepository adminRepository;
    
    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, StudentRepository studentRepository, ProfessorRepository professorRepository, AdminRepository adminRepository) {
        this.accountRepository = accountRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.adminRepository = adminRepository;
    }
    
    @Override
    public Account getAccount(@NonNull Long userID) throws InvalidRelationIdException {
        Optional<Account> accountOptional = accountRepository.findById(userID);
        
        if(!accountOptional.isPresent())
            throw new InvalidRelationIdException("Account ID (" + userID + ") does not exist in the database.");
        
        return accountOptional.get();
    }
    
    @Override
    public Student getStudent(@NonNull Long userID) throws InvalidRelationIdException {
        Optional<Student> studentOptional = studentRepository.findById(userID);
        
        if(!studentOptional.isPresent())
            throw new InvalidRelationIdException("Account ID (" + userID + ") does not exist in the database.");
        
        return studentOptional.get();
    }
    
    @Override
    public Professor getProfessor(@NonNull Long userID) throws InvalidRelationIdException {
        Optional<Professor> professorOptional = professorRepository.findById(userID);
        
        if(!professorOptional.isPresent())
            throw new InvalidRelationIdException("Account ID (" + userID + ") does not exist in the database.");
        
        return professorOptional.get();
    }
    
    @Override
    public CollegeAdmin getAdmin(@NonNull Long userID) throws InvalidRelationIdException {
        Optional<CollegeAdmin> adminOptional = adminRepository.findById(userID);
        
        if(!adminOptional.isPresent())
            throw new InvalidRelationIdException("Account ID (" + userID + ") does not exist in the database.");
        
        return adminOptional.get();
    }
    
    @Override
    public Account addAccount(@NonNull AccountModel account) throws RelationException, IllegalArgumentException {
        if(!emailPattern.matcher(account.getEduEmail()).matches())
            throw new IllegalArgumentException("Email is invalid.");
        
        if(account.getFirstName().length() == 0)
            throw new IllegalArgumentException("First name is invalid.");
        
        if(account.getLastName().length() == 0)
            throw new IllegalArgumentException("Last name is invalid.");
        
        Account savedAccount = accountRepository.save(new Account(account));
        
        if(savedAccount == null)
            throw new RelationException("Could not create new account object in database.");
        
        return savedAccount;
    }
    
    @Override
    public void removeAccount(@NonNull Long userID) throws InvalidRelationIdException {
        Account account = getAccount(userID);
        
        accountRepository.delete(account);
    }
    
    @Override
    public Account modifyAccount(@NonNull Account modifyAccount) throws InvalidRelationIdException {
        Account account = getAccount(modifyAccount.getUserID());
        
        if(account.getEduEmail() != null) {
            if(!emailPattern.matcher(account.getEduEmail()).matches())
                throw new IllegalArgumentException("Email provided is invalid.");
            
            account.setEduEmail(modifyAccount.getEduEmail());
        }
        
        if(account.getFirstName() != null) {
            if(account.getFirstName().length() == 0)
                throw new IllegalArgumentException("First name provided is invalid.");
            
            account.setFirstName(modifyAccount.getFirstName());
        }
        
        if(account.getLastName() != null) {
            if(account.getLastName().length() == 0)
                throw new IllegalArgumentException("Last name provided is invalid.");
            
            account.setLastName(modifyAccount.getLastName());
        }
        
        if(account.getRole() != null)
            account.setRole(modifyAccount.getRole());
        
        account = accountRepository.save(account);
        
        return account;
    }
}
