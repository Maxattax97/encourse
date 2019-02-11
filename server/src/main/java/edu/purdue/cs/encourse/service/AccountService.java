package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.model.AccountModel;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.InvalidRelationIdException;
import javax.management.relation.RelationException;

public interface AccountService {
	
	@Transactional(readOnly = true)
    Account getAccount(@NonNull Long userID) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
    Student getStudent(@NonNull Long userID) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
    Professor getProfessor(@NonNull Long userID) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
    CollegeAdmin getAdmin(@NonNull Long userID) throws InvalidRelationIdException;
	
	@Transactional
	Account addAccount(@NonNull AccountModel account) throws RelationException, IllegalArgumentException;
	
	@Transactional
	void removeAccount(@NonNull Long userID) throws InvalidRelationIdException;
	
	@Transactional
    Account modifyAccount(@NonNull Account modifyAccount) throws InvalidRelationIdException;

}
