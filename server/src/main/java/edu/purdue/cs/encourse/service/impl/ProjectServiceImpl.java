package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.AdditionHashRepository;
import edu.purdue.cs.encourse.database.CourseRepository;
import edu.purdue.cs.encourse.database.CourseStudentRepository;
import edu.purdue.cs.encourse.database.ProjectDateRepository;
import edu.purdue.cs.encourse.database.ProjectRepository;
import edu.purdue.cs.encourse.database.StudentComparisonRepository;
import edu.purdue.cs.encourse.database.StudentProjectDateRepository;
import edu.purdue.cs.encourse.database.StudentProjectRepository;
import edu.purdue.cs.encourse.database.StudentRepository;
import edu.purdue.cs.encourse.database.TestScriptRepository;
import edu.purdue.cs.encourse.database.TestSuiteRepository;
import edu.purdue.cs.encourse.domain.AdditionHash;
import edu.purdue.cs.encourse.domain.Commit;
import edu.purdue.cs.encourse.domain.Course;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.ProjectDate;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.TestScript;
import edu.purdue.cs.encourse.domain.TestSuite;
import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import edu.purdue.cs.encourse.domain.relations.StudentComparison;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.BasicStatistics;
import edu.purdue.cs.encourse.model.CourseProjectModel;
import edu.purdue.cs.encourse.model.ProjectIgnoreModel;
import edu.purdue.cs.encourse.model.ProjectModel;
import edu.purdue.cs.encourse.model.ProjectTestScriptModel;
import edu.purdue.cs.encourse.model.ProjectTestSuiteModel;
import edu.purdue.cs.encourse.model.TestScriptModel;
import edu.purdue.cs.encourse.model.TestSuiteModel;
import edu.purdue.cs.encourse.service.CourseServiceV2;
import edu.purdue.cs.encourse.service.ProjectService;
import edu.purdue.cs.encourse.service.helper.General;
import edu.purdue.cs.encourse.service.helper.TestExecuter;
import lombok.NonNull;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.InvalidRelationIdException;
import javax.management.relation.RelationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
@Service(value = ProjectServiceImpl.NAME)
public class ProjectServiceImpl implements ProjectService {
	
	final static String NAME = "ProjectService";
	
	private final ProjectRepository projectRepository;
	
	private final ProjectDateRepository projectDateRepository;
	
	private final StudentProjectRepository studentProjectRepository;
	
	private final StudentProjectDateRepository studentProjectDateRepository;
	
	private final CourseRepository courseRepository;
	
	private final TestScriptRepository testScriptRepository;
	
	private final TestSuiteRepository testSuiteRepository;
	
	private final CourseServiceV2 courseService;
	
	private final StudentComparisonRepository studentComparisonRepository;
	
	@Autowired
	public ProjectServiceImpl(ProjectRepository projectRepository, ProjectDateRepository projectDateRepository, StudentProjectRepository studentProjectRepository, StudentProjectDateRepository studentProjectDateRepository, CourseRepository courseRepository, TestScriptRepository testScriptRepository, TestSuiteRepository testSuiteRepository, CourseServiceV2 courseService, StudentComparisonRepository studentComparisonRepository) {
		this.projectRepository = projectRepository;
		this.projectDateRepository = projectDateRepository;
		this.studentProjectRepository = studentProjectRepository;
		this.studentProjectDateRepository = studentProjectDateRepository;
		this.courseRepository = courseRepository;
		this.testScriptRepository = testScriptRepository;
		this.testSuiteRepository = testSuiteRepository;
		this.courseService = courseService;
		this.studentComparisonRepository = studentComparisonRepository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Project getProject(@NonNull Long projectID) throws InvalidRelationIdException {
		Optional<Project> projectOptional = projectRepository.findById(projectID);
		
		if(!projectOptional.isPresent())
			throw new InvalidRelationIdException("Project ID (" + projectID + ") does not exist in the database.");
		
		return projectOptional.get();
	}
	
	@Override
	@Transactional(readOnly = true)
	public TestScript getTestScript(@NonNull Long testScriptID) throws InvalidRelationIdException {
		Optional<TestScript> testScriptOptional = testScriptRepository.findById(testScriptID);
		
		if(!testScriptOptional.isPresent())
			throw new InvalidRelationIdException("Test Script ID (" + testScriptID + ") does not exist in the database.");
		
		return testScriptOptional.get();
	}
	
	@Override
	@Transactional(readOnly = true)
	public TestSuite getTestSuite(@NonNull Long testSuiteID) throws InvalidRelationIdException {
		Optional<TestSuite> testSuiteOptional = testSuiteRepository.findById(testSuiteID);
		
		if(!testSuiteOptional.isPresent())
			throw new InvalidRelationIdException("Test Suite ID (" + testSuiteID + ") does not exist in the database.");
		
		return testSuiteOptional.get();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TestScript> getProjectTestScripts(@NonNull Long projectID) throws InvalidRelationIdException {
		return getProject(projectID).getTestScripts();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TestSuite> getProjectTestSuites(@NonNull Long projectID) throws InvalidRelationIdException {
		return getProject(projectID).getTestSuites();
	}
	
	@Override
	@Transactional
	public Project addProject(@NonNull CourseProjectModel model) throws InvalidRelationIdException, IllegalArgumentException, IOException, InterruptedException {
		if(model.getStartDate().compareTo(model.getDueDate()) >= 0)
			throw new IllegalArgumentException("");
		
		if(projectRepository.existsByNameEqualsAndCourse_CourseID(model.getName(), model.getCourseID()))
			throw new IllegalArgumentException("");
		
		Course course = courseService.getCourse(model.getCourseID());
		
		Project project = projectRepository.save(new Project(course, model));
		
		List<CourseStudent> students = course.getStudents();
		
		for(CourseStudent student : students) {
			if(student.getIsStudent()) {
				StudentProject studentProject = studentProjectRepository.save(new StudentProject(project, student));
				
				student.getProjects().add(studentProject);
				
				project.getStudentProjects().add(studentProject);
			}
		}
		
		List<StudentProject> studentProjects = project.getStudentProjects();
		
		LocalDate iteratorDate = project.getStartDate();
		
		while(iteratorDate.compareTo(project.getDueDate()) <= 0) {
			
			ProjectDate projectDate = projectDateRepository.save(new ProjectDate(project, iteratorDate));
			
			project.getDates().add(projectDate);
			
			System.out.println("Added Project Date (" + project.getRepository() + ", " + iteratorDate + ")");
			
			for(StudentProject studentProject : studentProjects) {
				StudentProjectDate studentProjectDate = studentProjectDateRepository.save(new StudentProjectDate(project, studentProject, iteratorDate));
				
				studentProject.getDates().add(studentProjectDate);
			}
			
			iteratorDate = iteratorDate.plusDays(1);
		}
		
		for(int i = 0; i < studentProjects.size(); i++) {
			for(int j = i + 1; j < studentProjects.size(); j++) {
				StudentProject studentProject1 = studentProjects.get(i);
				StudentProject studentProject2 = studentProjects.get(j);
				StudentComparison comparison = studentComparisonRepository.save(new StudentComparison(project, studentProject1, studentProject2, 0, 0.0));
				
				studentProject1.getFirstComparisons().add(comparison);
				studentProject2.getSecondComparisons().add(comparison);
				
				project.getStudentComparisons().add(comparison);
			}
		}
		
		course.getProjects().add(project);
		
		cloneProject(project);
		
		courseRepository.save(course);
		
		System.out.println("Added " + project);
		
		return project;
	}
	
	@Override
	@Transactional
	public void removeProject(@NonNull Long projectID) throws RelationException {
		Project project = getProject(projectID);
		Course course = project.getCourse();
		
		course.getProjects().remove(project);
		
		for(CourseStudent student : course.getStudents())
			student.getProjects().removeIf(studentProject -> studentProject.getProject().getProjectID().equals(project.getProjectID()));
		
		projectRepository.delete(project);
		
		course = courseRepository.save(course);
		
		if(course == null)
			throw new RelationException("");
	}
	
	@Override
	@Transactional
	public Project modifyProject(@NonNull ProjectModel model) throws RelationException, IllegalArgumentException {
		if(model.getStartDate().compareTo(model.getDueDate()) >= 0)
			throw new IllegalArgumentException("");
		
		if(model.getProjectID() == null)
			throw new IllegalArgumentException("");
		
		Project project = getProject(model.getProjectID());
		
		project.setName(model.getName());
		project.setStartDate(model.getStartDate());
		project.setDueDate(model.getDueDate());
		project.setRepository(model.getRepository());
		
		project = projectRepository.save(project);
		
		if(project == null)
			throw new RelationException("");
		
		return project;
	}
	
	@Override
	@Transactional
	public TestScript addTestScript(@NonNull ProjectTestScriptModel model) throws RelationException, IllegalArgumentException {
		if(model.getName().startsWith("-") || model.getName().startsWith("+"))
			throw new IllegalArgumentException("");
		
		model.setName(model.getName().replace(' ', '_'));
		
		if(model.getName().startsWith("_"))
			throw new IllegalArgumentException("");
		
		if(testScriptRepository.existsByNameEqualsAndProject_ProjectID(model.getName(), model.getProjectID()))
			throw new IllegalArgumentException("");
		
		Project project = getProject(model.getProjectID());
		
		TestScript testScript = new TestScript(project, model);
		
		project.getTestScripts().add(testScript);
		
		if(testScript.getHidden())
			project.setTotalHiddenPoints(project.getTotalHiddenPoints() + testScript.getValue());
		else
			project.setTotalVisiblePoints(project.getTotalVisiblePoints() + testScript.getValue());
		
		project = projectRepository.save(project);
		
		if(project == null)
			throw new RelationException("");
		
		return testScript;
	}
	
	@Override
	@Transactional
	public void removeTestScript(@NonNull Long testScriptID) throws RelationException {
		TestScript testScript = getTestScript(testScriptID);
		Project project = testScript.getProject();
		
		project.getTestScripts().remove(testScript);
		
		project = projectRepository.save(project);
		
		if(project == null)
			throw new RelationException("");
	}
	
	@Override
	@Transactional
	public TestScript modifyTestScript(@NonNull TestScriptModel model) throws RelationException, IllegalArgumentException {
		if(model.getTestScriptID() == null)
			throw new IllegalArgumentException("");
		
		TestScript testScript = getTestScript(model.getTestScriptID());
		
		testScript.setName(model.getName());
		testScript.setHidden(model.getHidden());
		testScript.setValue(model.getValue());
		
		testScript = testScriptRepository.save(testScript);
		
		if(testScript == null)
			throw new RelationException("");
		
		return testScript;
	}
	
	@Override
	@Transactional
	public TestSuite addTestSuite(@NonNull ProjectTestSuiteModel model) throws RelationException, IllegalArgumentException {
		if(testSuiteRepository.existsByNameEqualsAndProject_ProjectID(model.getName(), model.getProjectID()))
			throw new IllegalArgumentException("");
		
		Project project = getProject(model.getProjectID());
		
		TestSuite testSuite = new TestSuite(project, model);
		
		project.getTestSuites().add(testSuite);
		
		project = projectRepository.save(project);
		
		if(project == null)
			throw new RelationException("");
		
		return testSuite;
	}
	
	@Override
	@Transactional
	public void removeTestSuite(Long testSuiteID) throws RelationException {
		TestSuite testSuite = getTestSuite(testSuiteID);
		Project project = testSuite.getProject();
		
		project.getTestSuites().remove(testSuite);
		
		project = projectRepository.save(project);
		
		if(project == null)
			throw new RelationException("");
	}
	
	@Override
	@Transactional
	public TestSuite modifyTestSuite(@NonNull TestSuiteModel model) throws RelationException, IllegalArgumentException {
		if(model.getTestSuiteID() == null)
			throw new IllegalArgumentException("");
		
		TestSuite testSuite = getTestSuite(model.getTestSuiteID());
		
		testSuite.setName(model.getName());
		testSuite.setHidden(model.getHidden());
		
		testSuite = testSuiteRepository.save(testSuite);
		
		if(testSuite == null)
			throw new RelationException("");
		
		return testSuite;
	}
	
	@Override
	@Transactional
	public void addProjectIgnoreUser(@NonNull ProjectIgnoreModel model) throws RelationException {
		Project project = getProject(model.getProjectID());
		
		project.getIgnoredUsers().add(model.getUser());
		
		projectRepository.save(project);
	}
	
	private void executeScript(@NonNull String command) throws InterruptedException, IOException {
		Process process = Runtime.getRuntime().exec("./src/main/bash/" + command + " 2> /dev/null");
		process.waitFor();
	}
	
	private void cloneProject(Project project) throws IOException, InterruptedException {
		Course course = project.getCourse();
		List<StudentProject> projects = project.getStudentProjects();
		
		for(StudentProject p : projects) {
			Student s = p.getStudent().getStudent();
			if(!(new File(course.getCourseHub() + "/" + s.getUsername() + "/" + project.getRepository()).exists())) {
				String destPath = (course.getCourseHub() + "/" + s.getUsername() + "/" + project.getRepository());
				String repoPath = (course.getRemotePath() + "/" + s.getUsername() + "/" + project.getRepository() + ".git");
				executeScript("cloneRepositories.sh " + destPath + " " + repoPath);
			}
		}
		
		executeScript("setPermissions.sh " + course.getCourseID());
	}
	
}
