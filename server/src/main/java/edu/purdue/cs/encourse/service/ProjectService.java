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

import javax.management.relation.InvalidRelationIdException;
import javax.management.relation.RelationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
public interface ProjectService {
	
	Project getProject(@NonNull Long projectID) throws InvalidRelationIdException;
	
	TestScript getTestScript(@NonNull Long testScriptID) throws InvalidRelationIdException;
	
	TestSuite getTestSuite(@NonNull Long testSuiteID) throws InvalidRelationIdException;
	
	List<TestScript> getProjectTestScripts(@NonNull Long projectID) throws InvalidRelationIdException;
	
	List<TestSuite> getProjectTestSuites(@NonNull Long projectID) throws InvalidRelationIdException;
	
	Project addProject(@NonNull CourseProjectModel model) throws InvalidRelationIdException, IllegalArgumentException, IOException, InterruptedException;
	
	void removeProject(@NonNull Long projectID) throws RelationException;
	
	Project modifyProject(@NonNull ProjectModel model) throws RelationException, IllegalArgumentException;
	
	TestScript addTestScript(@NonNull ProjectTestScriptModel model) throws RelationException;
	
	void removeTestScript(@NonNull Long testScriptID) throws RelationException;
	
	TestScript modifyTestScript(@NonNull TestScriptModel model) throws RelationException, IllegalArgumentException;
	
	TestSuite addTestSuite(@NonNull ProjectTestSuiteModel model) throws RelationException;
	
	void removeTestSuite(Long testSuiteID) throws RelationException;
	
	TestSuite modifyTestSuite(@NonNull TestSuiteModel model) throws RelationException, IllegalArgumentException;
	
	void analyzeProjects();
	
}
