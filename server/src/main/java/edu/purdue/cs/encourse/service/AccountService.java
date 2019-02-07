package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.*;
import edu.purdue.cs.encourse.model.AccountModel;
import lombok.NonNull;

import javax.management.relation.InvalidRelationIdException;
import javax.management.relation.RelationException;

public interface AccountService {
    
    Account getAccount(@NonNull Long userID) throws InvalidRelationIdException;
    
    Student getStudent(@NonNull Long userID) throws InvalidRelationIdException;
    
    Professor getProfessor(@NonNull Long userID) throws InvalidRelationIdException;
    
    CollegeAdmin getAdmin(@NonNull Long userID) throws InvalidRelationIdException;
	
	Account addAccount(@NonNull AccountModel account) throws RelationException, IllegalArgumentException;
	
	void removeAccount(@NonNull Long userID) throws InvalidRelationIdException;
    
    Account modifyAccount(@NonNull Account modifyAccount) throws InvalidRelationIdException;

}
