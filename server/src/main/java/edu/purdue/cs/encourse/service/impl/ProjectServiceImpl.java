package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.AdditionHashRepository;
import edu.purdue.cs.encourse.database.CourseRepository;
import edu.purdue.cs.encourse.database.CourseStudentRepository;
import edu.purdue.cs.encourse.database.ProjectDateRepository;
import edu.purdue.cs.encourse.database.ProjectRepository;
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
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.BasicStatistics;
import edu.purdue.cs.encourse.model.CourseProjectModel;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
	
	private final AdditionHashRepository additionHashRepository;
	
	private final CourseServiceV2 courseService;
	
	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss").withLocale(Locale.US);
	
	@Autowired
	public ProjectServiceImpl(ProjectRepository projectRepository, ProjectDateRepository projectDateRepository, StudentProjectRepository studentProjectRepository, StudentProjectDateRepository studentProjectDateRepository, CourseRepository courseRepository, TestScriptRepository testScriptRepository, TestSuiteRepository testSuiteRepository, AdditionHashRepository additionHashRepository, CourseServiceV2 courseService) {
		this.projectRepository = projectRepository;
		this.projectDateRepository = projectDateRepository;
		this.studentProjectRepository = studentProjectRepository;
		this.studentProjectDateRepository = studentProjectDateRepository;
		this.courseRepository = courseRepository;
		this.testScriptRepository = testScriptRepository;
		this.testSuiteRepository = testSuiteRepository;
		this.additionHashRepository = additionHashRepository;
		this.courseService = courseService;
	}
	
	@Override
	public Project getProject(@NonNull Long projectID) throws InvalidRelationIdException {
		Optional<Project> projectOptional = projectRepository.findById(projectID);
		
		if(!projectOptional.isPresent())
			throw new InvalidRelationIdException("Project ID (" + projectID + ") does not exist in the database.");
		
		return projectOptional.get();
	}
	
	@Override
	public TestScript getTestScript(@NonNull Long testScriptID) throws InvalidRelationIdException {
		Optional<TestScript> testScriptOptional = testScriptRepository.findById(testScriptID);
		
		if(!testScriptOptional.isPresent())
			throw new InvalidRelationIdException("Test Script ID (" + testScriptID + ") does not exist in the database.");
		
		return testScriptOptional.get();
	}
	
	@Override
	public TestSuite getTestSuite(@NonNull Long testSuiteID) throws InvalidRelationIdException {
		Optional<TestSuite> testSuiteOptional = testSuiteRepository.findById(testSuiteID);
		
		if(!testSuiteOptional.isPresent())
			throw new InvalidRelationIdException("Test Suite ID (" + testSuiteID + ") does not exist in the database.");
		
		return testSuiteOptional.get();
	}
	
	@Override
	public List<TestScript> getProjectTestScripts(@NonNull Long projectID) throws InvalidRelationIdException {
		return getProject(projectID).getTestScripts();
	}
	
	@Override
	public List<TestSuite> getProjectTestSuites(@NonNull Long projectID) throws InvalidRelationIdException {
		return getProject(projectID).getTestSuites();
	}
	
	@Override
	public Project addProject(@NonNull CourseProjectModel model) throws InvalidRelationIdException, IllegalArgumentException, IOException, InterruptedException {
		if(model.getStartDate().compareTo(model.getDueDate()) >= 0)
			throw new IllegalArgumentException("");
		
		Course course = courseService.getCourse(model.getCourseID());
		
		Project project = new Project(course, model);
		
		List<CourseStudent> students = course.getStudents();
		
		for(CourseStudent student : students) {
			StudentProject studentProject = new StudentProject(project, student);
			
			student.getProjects().add(studentProject);
			
			project.getStudentProjects().add(studentProject);
		}
		
		List<StudentProject> studentProjects = project.getStudentProjects();
		
		LocalDate iteratorDate = project.getStartDate();
		
		while(iteratorDate.compareTo(project.getDueDate()) != 0) {
			
			project.getDates().add(new ProjectDate(project, iteratorDate));
			
			for(StudentProject studentProject : studentProjects) {
				StudentProjectDate studentProjectDate = new StudentProjectDate(project, studentProject, iteratorDate);
				
				studentProject.getDates().add(studentProjectDate);
			}
			
			iteratorDate = iteratorDate.plusDays(1);
		}
		
		course.getProjects().add(project);
		
		cloneProject(project);
		
		courseRepository.save(course);
		
		return project;
	}
	
	@Override
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
	public TestScript addTestScript(@NonNull ProjectTestScriptModel model) throws RelationException {
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
	public void removeTestScript(@NonNull Long testScriptID) throws RelationException {
		TestScript testScript = getTestScript(testScriptID);
		Project project = testScript.getProject();
		
		project.getTestScripts().remove(testScript);
		
		project = projectRepository.save(project);
		
		if(project == null)
			throw new RelationException("");
	}
	
	@Override
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
	public TestSuite addTestSuite(@NonNull ProjectTestSuiteModel model) throws RelationException {
		Project project = getProject(model.getProjectID());
		
		TestSuite testSuite = new TestSuite(project, model);
		
		project.getTestSuites().add(testSuite);
		
		project = projectRepository.save(project);
		
		if(project == null)
			throw new RelationException("");
		
		return testSuite;
	}
	
	@Override
	public void removeTestSuite(Long testSuiteID) throws RelationException {
		TestSuite testSuite = getTestSuite(testSuiteID);
		Project project = testSuite.getProject();
		
		project.getTestSuites().remove(testSuite);
		
		project = projectRepository.save(project);
		
		if(project == null)
			throw new RelationException("");
	}
	
	@Override
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
	
	private void executeScript(@NonNull String command) throws InterruptedException, IOException {
		Process process = Runtime.getRuntime().exec("./src/main/bash/" + command + " 2> /dev/null");
		process.waitFor();
	}
	
	private Process executeScriptAndReturn(@NonNull String command) throws IOException {
		return Runtime.getRuntime().exec("./src/main/bash/" + command + " 2> /dev/null");
	}
	
	private void cloneProject(Project project) throws IOException, InterruptedException {
		Course course = project.getCourse();
		List<StudentProject> projects = project.getStudentProjects();
		
		for(StudentProject p : projects) {
			Student s = p.getStudent().getStudent();
			if(!(new File(course.getCourseHub() + "/" + s.getUsername() + "/" + project.getRepository()).exists())) {
				String destPath = (course.getCourseHub() + "/" + p.getId());
				String repoPath = (course.getRemotePath() + "/" + s.getUsername() + "/" + project.getRepository() + ".git");
				executeScript("cloneRepositories.sh " + destPath + " " + repoPath);
			}
		}
		
		executeScript("setPermissions.sh " + course.getCourseID());
	}
	
	private void pullProject(Project project) throws IOException, InterruptedException {
		Course course = project.getCourse();
		List<StudentProject> projects = project.getStudentProjects();
		
		for(StudentProject p : projects) {
			String destPath = (course.getCourseHub() + "/" + p.getId());
			executeScript("pullRepositories.sh " + destPath);
		}
		
		executeScript("setPermissions.sh " + course.getCourseID());
	}
	
	/*
	 * Runs a testall process on a specific commit provided through the String "line"
	 */
	private void runStudentTestall(Project project, List<StudentProjectDate> studentProjectDates, Map<String, TestScript> testScripts, String line, String testingDirectory, File testCaseDirectory, File hiddenTestCaseDirectory, File makefile) throws InterruptedException, IOException {
		if(line == null)
			return;
		
		String[] commitInfo = line.split(" ");
		
		if(commitInfo.length == 0)
			return;
		
		//Parse the splitted line 3rd value, that being the local date or the yyyy-MM-dd
		LocalDate date = LocalDate.parse(commitInfo[2], dtf);
		
		//checkout to previous commit based on the 2nd value of line, then run the makefile bash script
		executeScript("checkoutPreviousCommit.sh " + testingDirectory + " " + commitInfo[1]);
		executeScript("runMakefile.sh " + testingDirectory + " " + makefile.getPath());
		
		//create the testing environment, run it, then return the values retrieved from the execution
		TestExecuter tester = new TestExecuter(project.getCourseName(), testingDirectory + "/" + project.getProjectID(), testCaseDirectory.getPath(), hiddenTestCaseDirectory.getPath());
		
		tester.start();
		Thread.sleep(5000);
		tester.interrupt();
		
		//kill all processes produced during the testexecutor lifecycle
		executeScript("killProcesses.sh " + project.getCourseID());
		
		//convert the test score from the testexecutor into real values used by the project
		tester.parse(testScripts);
		
		//determine which test score was better, set the student project to contain that value
		/*if(tester.getVisiblePoints() + tester.getHiddenPoints() >= studentProject.getVisiblePoints() + studentProject.getHiddenPoints()) {
			studentProject.setTestsPassing(tester.getPassedTests());
			
			studentProject.setVisiblePoints(tester.getVisiblePoints());
			studentProject.setHiddenPoints(tester.getHiddenPoints());
		}*/
		
		//find the student project date object for the given date retrieved inside the line string
		StudentProjectDate studentProjectDate = null;
		
		for (StudentProjectDate dates : studentProjectDates) {
			if (dates.getDate() == date) {
				studentProjectDate = dates;
				break;
			}
		}
		
		//if none is found, create it and add it to the list
		if(studentProjectDate == null) {
			//studentProjectDate = new StudentProjectDate(studentProject, date);
			//studentProjectDates.add(studentProjectDate);
		}
		
		//determine which test score was better, set the student project date to contain that value
		if(tester.getVisiblePoints() + tester.getHiddenPoints() >= studentProjectDate.getVisiblePoints() + studentProjectDate.getHiddenPoints()) {
			studentProjectDate.setTestsPassing(tester.getPassedTests());
			
			studentProjectDate.setVisiblePoints(tester.getVisiblePoints());
			studentProjectDate.setHiddenPoints(tester.getHiddenPoints());
		}
	}
	
	private void runAllStudentTestalls(Project project, Map<String, TestScript> testScripts, List<StudentProject> studentProjects, Map<StudentProject, List<StudentProjectDate>> studentProjectListMap) {
		
		File testCaseDirectory = new File(project.getCourse().getCourseHub() + "/testcases/" + project.getRepository());
		File hiddenTestCaseDirectory = new File(project.getCourse().getCourseHub() + "/hidden_testcases/" + project.getRepository());
		File makefile = new File(project.getCourse().getCourseHub() + "/makefiles/" + project.getRepository() + "/Makefile");
		
		if(!testCaseDirectory.isDirectory() || testCaseDirectory.listFiles().length == 0)
			return;
		
		if(!makefile.exists())
			return;
		
		int processedStudentProjects = 0;
		long startTime = System.currentTimeMillis();
		
		String courseHub = project.getCourse().getCourseHub();
		
		for(StudentProject studentProject : studentProjects) {
			
			String testingDirectory = courseHub + "/" + studentProject.getId();
			
			try {
				executeScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
				
				Process process = executeScriptAndReturn("listTestUpdateHistory.sh " + testingDirectory);
				
				String[] buildCommits = General.readFirst(process.getInputStream(), 2);
				
				process.waitFor();
				
				runStudentTestall(project, studentProjectListMap.get(studentProject), testScripts, buildCommits[0], testingDirectory, testCaseDirectory, hiddenTestCaseDirectory, makefile);
				runStudentTestall(project, studentProjectListMap.get(studentProject), testScripts, buildCommits[1], testingDirectory, testCaseDirectory, hiddenTestCaseDirectory, makefile);
				
				executeScript("checkoutPreviousCommit.sh " + testingDirectory + " origin");
				
				processedStudentProjects++;
				
				project.setOperationProgress(((double) processedStudentProjects) / studentProjects.size());
				project.setOperationTime((System.currentTimeMillis() - startTime) / 1000);
				
				projectRepository.save(project);
			}
			catch(Exception e) {
				processedStudentProjects++;
				
				project.setOperationProgress(((double) processedStudentProjects) / studentProjects.size());
				project.setOperationTime((System.currentTimeMillis() - startTime) / 1000);
				
				projectRepository.save(project);
				
				System.out.println("\nException at testall\n");
			}
		}
	}
	
	private List<Commit> createCommitObjects(Project project, CourseStudent student, Map<String, AdditionHash> additionHashMap, MessageDigest md5) throws IOException, InterruptedException {
		List<Commit> commitList = new ArrayList<>(30);
		
		//TODO Call proper bash script
		Process process = executeScriptAndReturn("THE_NEW");
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		String line;
		
		Commit commit = null;
		
		boolean measureChanges = false;
		int additions = 0;
		int deletions = 0;
		
		String hash;
		AdditionHash additionHash;
		Integer count;
		
		//FORMAT
		//@DIFF,<commit_hash>,<date in YYYY-MM-dd>
		//+++<FILE_NAME.FILE_DESCRIPTOR>
		//+<source code line>
		
		//read all lines from the executed process above
		while((line = reader.readLine()) != null) {
			//first check if we have the @DIFF command to indicate we are discussing a new commit
			if(line.startsWith("@DIFF")) {
				String[] split = line.split(",");
				
				if(commit != null) {
					commit.setAdditions(commit.getAdditions() + additions);
					commit.setDeletions(commit.getDeletions() + deletions);
					
					if(commit.getAdditions() != 0 || commit.getDeletions() != 0)
						commitList.add(commit);
				}
				
				additions = 0;
				deletions = 0;
				
				commit = new Commit(split[1], LocalDateTime.parse(split[2], dtf), Double.MIN_NORMAL, Double.MIN_NORMAL, Double.MIN_NORMAL, Double.MIN_NORMAL);
			}
			else if(commit != null) {
				if(line.startsWith("+++")) {
					measureChanges = General.isSourceCodeExtension(line);
					
					if(additions < 500)
						commit.setAdditions(commit.getAdditions() + additions);
					if(deletions < 500)
						commit.setDeletions(commit.getDeletions() + deletions);
					
					additions = deletions = 0;
				}
				else if(measureChanges && line.length() > 1) {
					if(line.charAt(0) == '+') {
						additions++;
						line = line.substring(1);
						
						hash = new BigInteger(1, md5.digest(line.getBytes())).toString();
						
						additionHash = additionHashMap.get(hash);
						
						if(additionHash == null) {
							additionHash = new AdditionHash(hash, project, new HashMap<>());
							additionHash.getStudentCounts().put(student.getId(), 1);
							
							additionHashMap.put(hash, additionHash);
						}
						else {
							count = additionHash.getStudentCounts().get(student.getId());
							
							additionHash.getStudentCounts().put(student.getId(), count == null ? 1 : count + 1);
						}
					}
					else if(line.charAt(0) == '-' && line.charAt(1) != '-')
						deletions++;
				}
			}
		}
		
		if(commit != null) {
			commit.setAdditions(commit.getAdditions() + additions);
			commit.setDeletions(commit.getDeletions() + deletions);
			
			commitList.add(commit);
		}
		
		process.waitFor();
		
		return commitList;
	}
	
	private void calculateStudentDiffs(Project project, List<ProjectDate> projectDateList, Map<StudentProject, List<StudentProjectDate>> studentProjectListMap) {
		//Hashing algorithm for cheat detection
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		}
		catch(NoSuchAlgorithmException e) {
			return;
		}
		
		Map<String, AdditionHash> additionHashMap = new HashMap<>();
		
		//iterate through all students in the project
		for(StudentProject studentProject : studentProjectListMap.keySet()) {
			CourseStudent student = studentProject.getStudent();
			
			//ensure that all keys are present inside the map
			if(studentProjectListMap.containsKey(studentProject))
				studentProjectListMap.put(studentProject, new ArrayList<>());
			
			List<StudentProjectDate> studentProjectDateList = studentProjectListMap.get(studentProject);
			
			//makes sure that the first element in the list is the earliest student project date
			studentProjectDateList.sort(Comparator.comparing(StudentProjectDate::getDate));
			
			List<Commit> commitList = null;
			
			try {
				//collect all commits and sort
				commitList = createCommitObjects(project, student, additionHashMap, md5);
			}
			catch(IOException | InterruptedException e) {
			
			}
			
			if(commitList == null)
				continue;
			
			commitList.sort(Comparator.comparing(Commit::getDate));
			
			LocalDateTime previousCommitTime = studentProject.getMostRecentCommit();
			
			//make sure previouscommittime is not null
			if(previousCommitTime == null)
				previousCommitTime = LocalDateTime.MIN;
			
			ProjectDate projectDate;
			StudentProjectDate studentProjectDate = studentProjectDateList.get(0);
			
			//reset the earliest student project date totals, the reason being that we will be altering the currents that impact the totals
			//so when we change the currents, we can add the new currents to the totals again to retrieve the current values
			studentProjectDate.setTotalCommits(studentProjectDate.getTotalCommits() - studentProjectDate.getCurrentCommits());
			studentProjectDate.setTotalAdditions(studentProjectDate.getTotalAdditions() - studentProjectDate.getCurrentAdditions());
			studentProjectDate.setTotalDeletions(studentProjectDate.getTotalDeletions() - studentProjectDate.getCurrentDeletions());
			studentProjectDate.setTotalMinutes(studentProjectDate.getTotalMinutes() - studentProjectDate.getCurrentMinutes());
			
			//iterate through every commit
			for(Commit commit : commitList) {
				//find the distance between the previous commit (earlier in time) and the current
				//this distance is measured in minutes
				long time = Math.max(ChronoUnit.MINUTES.between(previousCommitTime, commit.getDate()), 0);
				
				//run the total time spent on the project between these two commits algorithm
				if(time > Math.max(commit.getAdditions() * 3, 10))
					time = Math.round(commit.getAdditions());
				
				//make sure that the studentProject first commit is the earliest and not null
				if(studentProject.getFirstCommit() == null || studentProject.getFirstCommit().compareTo(commit.getDate()) > 0)
					studentProject.setFirstCommit(commit.getDate());
				
				//add the commit additions/deletions and minutes
				/*studentProject.setAdditions(studentProject.getAdditions() + commit.getAdditions());
				studentProject.setDeletions(studentProject.getDeletions() + commit.getDeletions());
				studentProject.setMinutes(studentProject.getMinutes() + time);*/
				
				//find the student project date
				studentProjectDate = null;
				
				for(StudentProjectDate studentProjectDate1 : studentProjectDateList) {
					if (studentProjectDate1.getDate() == commit.getDate().toLocalDate()) {
						studentProjectDate = studentProjectDate1;
						break;
					}
				}
				
				//update commit count, additions/deletions, and minutes for the specific date
				studentProjectDate.setCurrentCommits(studentProjectDate.getCurrentCommits() + 1);
				studentProjectDate.setCurrentAdditions(studentProjectDate.getCurrentAdditions() + commit.getAdditions());
				studentProjectDate.setCurrentDeletions(studentProjectDate.getCurrentDeletions() + commit.getDeletions());
				studentProjectDate.setCurrentMinutes(studentProjectDate.getCurrentMinutes() + time);
				
				//iteration with access to the previousCommitTime, important for time calculation and setting most recent commit
				previousCommitTime = commit.getDate();
			}
			
			//set most recent commit to latest commit
			if(studentProject.getMostRecentCommit() == null || studentProject.getMostRecentCommit().compareTo(previousCommitTime) <= 0) {
				studentProject.setMostRecentCommit(previousCommitTime);
				studentProject.setLastUpdatedCommit(commitList.get(commitList.size() - 1).getHash());
			}
			
			//update student project information
			//studentProject.setCommitCount(studentProject.getCommitCount() + commitList.size());
			
			//add all found commits to student project
			studentProject.getCommits().addAll(commitList);
			//studentProject.setCommitCount((double) studentProject.getCommits().size());
			
			StudentProjectDate previousStudentDate = null;
			
			//resort in case any new objects were inserted
			studentProjectDateList.sort(Comparator.comparing(StudentProjectDate::getDate));
			
			//iterate over the studentProjectDateList, setting the new totals properly
			for(int i = 0; i < studentProjectDateList.size(); i++) {
				studentProjectDate = studentProjectDateList.get(i);
				
				if(i == 0)
					previousStudentDate = studentProjectDate;
				
				studentProjectDate.setTotalCommits(previousStudentDate.getTotalCommits() + studentProjectDate.getCurrentCommits());
				studentProjectDate.setTotalMinutes(previousStudentDate.getTotalMinutes() + studentProjectDate.getCurrentMinutes());
				studentProjectDate.setTotalAdditions(previousStudentDate.getTotalAdditions() + studentProjectDate.getCurrentAdditions());
				studentProjectDate.setTotalDeletions(previousStudentDate.getTotalDeletions() + studentProjectDate.getCurrentDeletions());
				
				previousStudentDate = studentProjectDate;
			}
		}
		
		Map<LocalDate, List<StudentProjectDate>> dateToStudentDateMap = new HashMap<>(300);
		
		for(List<StudentProjectDate> studentProjectDates : studentProjectListMap.values()) {
			for(StudentProjectDate studentProjectDate : studentProjectDates) {
				List<StudentProjectDate> studentDateMapList = dateToStudentDateMap.get(studentProjectDate.getDate());
				
				if(studentDateMapList == null)
					dateToStudentDateMap.put(studentProjectDate.getDate(), Collections.singletonList(studentProjectDate));
				
				studentDateMapList.add(studentProjectDate);
			}
		}
		
		for(ProjectDate projectDate : projectDateList) {
			List<StudentProjectDate> studentProjectDateList = dateToStudentDateMap.get(projectDate.getDate());
			
			if(studentProjectDateList == null)
				continue;
			
			DescriptiveStatistics totalPointStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics visiblePointStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics hiddenPointStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics commitStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics minuteStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics additionStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics deletionStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics changesStats = new DescriptiveStatistics(studentProjectDateList.size());
			//TODO Similarity
			DescriptiveStatistics timeVelocityStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics commitVelocityStats = new DescriptiveStatistics(studentProjectDateList.size());
			
			for(StudentProjectDate studentProjectDate : studentProjectDateList) {
				totalPointStats.addValue(studentProjectDate.getVisiblePoints() + studentProjectDate.getHiddenPoints());
				visiblePointStats.addValue(studentProjectDate.getVisiblePoints());
				hiddenPointStats.addValue(studentProjectDate.getHiddenPoints());
				commitStats.addValue(studentProjectDate.getTotalCommits());
				minuteStats.addValue(studentProjectDate.getTotalMinutes());
				additionStats.addValue(studentProjectDate.getTotalAdditions());
				deletionStats.addValue(studentProjectDate.getTotalDeletions());
				changesStats.addValue(studentProjectDate.getTotalAdditions() / studentProjectDate.getTotalDeletions());
				//TODO Similarity
				timeVelocityStats.addValue((studentProjectDate.getVisiblePoints() + studentProjectDate.getHiddenPoints()) / studentProjectDate.getTotalMinutes());
				commitVelocityStats.addValue((studentProjectDate.getVisiblePoints() + studentProjectDate.getHiddenPoints()) / studentProjectDate.getTotalMinutes());
			}
			
			projectDate.setTotalPointStats(new BasicStatistics(totalPointStats));
			projectDate.setVisiblePointStats(new BasicStatistics(visiblePointStats));
			projectDate.setHiddenPointStats(new BasicStatistics(hiddenPointStats));
			projectDate.setCommitStats(new BasicStatistics(commitStats));
			projectDate.setMinuteStats(new BasicStatistics(minuteStats));
			projectDate.setAdditionStats(new BasicStatistics(additionStats));
			projectDate.setDeletionStats(new BasicStatistics(deletionStats));
			projectDate.setChangesStats(new BasicStatistics(changesStats));
			//TODO Similarity
			projectDate.setTimeVelocityStats(new BasicStatistics(timeVelocityStats));
			projectDate.setCommitVelocityStats(new BasicStatistics(commitVelocityStats));
		}
	}
	
	@Override
	public void analyzeProjects() {
		LocalDate currentDate = LocalDate.now();
		
		//Find all projects in database that have an analyze date time that is less than the due date and less than or equal to today's date
		List<Project> projects = projectRepository.findAllProjectsByAnalyzeDate();
		
		//Iterate over all found projects. we state the List<Project> cannot be null, but can be empty.
		for(Project project : projects) {
			if(project.getStartDate().compareTo(project.getDueDate()) == 0)
				continue;
			
			List<StudentProjectDate> studentProjectDates = studentProjectDateRepository.findByProjectAndDateGreaterThanEqual(project, project.getAnalyzeDateTime());
			
			if(studentProjectDates == null || studentProjectDates.isEmpty())
				continue;
			
			List<ProjectDate> projectDateList = projectDateRepository.findAllByProjectAndDateGreaterThanEqual(project, project.getAnalyzeDateTime());
			
			if(projectDateList == null || projectDateList.isEmpty())
				continue;
			
			try {
				pullProject(project);
			}
			catch(Exception e) {
				continue;
			}
			
			//Collect the projects test scripts (which are lazily retrieved) and map them to the test name -> test script
			Map<String, TestScript> testScripts = project.getTestScripts().stream().collect(Collectors.toMap(TestScript::getName, Function.identity()));
			
			List<StudentProject> studentProjects = project.getStudentProjects();
			
			Map<StudentProject, List<StudentProjectDate>> studentProjectListMap = studentProjectDates.stream().collect(Collectors.groupingBy(StudentProjectDate::getStudentProject));
			
			//start processing the project
			project.setAnalyzing(true);
			project.setAnalyzeDateTime(currentDate);
			projectRepository.save(project);
			
			runAllStudentTestalls(project, testScripts, studentProjects, studentProjectListMap);
			
			//populate all information not associated with the testall into the appropriate locations
			calculateStudentDiffs(project, projectDateList, studentProjectListMap);
			
			//save the ProjectDate Objects
			projectDateRepository.saveAll(projectDateList);
			
			//save the StudentProjectDate objects
			studentProjectDateRepository.saveAll(studentProjectDates);
			
			//save the StudentProject objects
			studentProjectRepository.saveAll(studentProjects);
			
			//stop analyzing the project and set the last time the project has been analyzed to today's date
			project.setAnalyzing(false);
			projectRepository.save(project);
		}
	}
}
