package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.TestScript;
import edu.purdue.cs.encourse.domain.TestSuite;
import edu.purdue.cs.encourse.model.CourseProjectModel;
import edu.purdue.cs.encourse.model.ProjectModel;
import edu.purdue.cs.encourse.model.ProjectTestScriptModel;
import edu.purdue.cs.encourse.model.ProjectTestSuiteModel;
import edu.purdue.cs.encourse.model.TestScriptModel;
import edu.purdue.cs.encourse.model.TestSuiteModel;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.InvalidRelationIdException;
import javax.management.relation.RelationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
public interface ProjectService {
	
	@Transactional(readOnly = true)
	Project getProject(@NonNull Long projectID) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
	TestScript getTestScript(@NonNull Long testScriptID) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
	TestSuite getTestSuite(@NonNull Long testSuiteID) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
	List<TestScript> getProjectTestScripts(@NonNull Long projectID) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
	List<TestSuite> getProjectTestSuites(@NonNull Long projectID) throws InvalidRelationIdException;
	
	@Transactional
	Project addProject(@NonNull CourseProjectModel model) throws InvalidRelationIdException, IllegalArgumentException, IOException, InterruptedException;
	
	@Transactional
	void removeProject(@NonNull Long projectID) throws RelationException;
	
	@Transactional
	Project modifyProject(@NonNull ProjectModel model) throws RelationException, IllegalArgumentException;
	
	@Transactional
	TestScript addTestScript(@NonNull ProjectTestScriptModel model) throws RelationException;
	
	@Transactional
	void removeTestScript(@NonNull Long testScriptID) throws RelationException;
	
	@Transactional
	TestScript modifyTestScript(@NonNull TestScriptModel model) throws RelationException, IllegalArgumentException;
	
	@Transactional
	TestSuite addTestSuite(@NonNull ProjectTestSuiteModel model) throws RelationException;
	
	@Transactional
	void removeTestSuite(Long testSuiteID) throws RelationException;
	
	@Transactional
	TestSuite modifyTestSuite(@NonNull TestSuiteModel model) throws RelationException, IllegalArgumentException;
	
}
