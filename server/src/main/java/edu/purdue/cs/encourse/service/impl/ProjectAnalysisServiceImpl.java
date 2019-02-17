package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.AdditionHashRepository;
import edu.purdue.cs.encourse.database.CourseRepository;
import edu.purdue.cs.encourse.database.ProjectDateRepository;
import edu.purdue.cs.encourse.database.ProjectRepository;
import edu.purdue.cs.encourse.database.StudentComparisonRepository;
import edu.purdue.cs.encourse.database.StudentProjectDateRepository;
import edu.purdue.cs.encourse.database.StudentProjectRepository;
import edu.purdue.cs.encourse.database.TestScriptRepository;
import edu.purdue.cs.encourse.database.TestSuiteRepository;
import edu.purdue.cs.encourse.domain.AdditionHash;
import edu.purdue.cs.encourse.domain.Commit;
import edu.purdue.cs.encourse.domain.Course;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.ProjectDate;
import edu.purdue.cs.encourse.domain.TestScript;
import edu.purdue.cs.encourse.domain.relations.StudentComparison;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.BasicStatistics;
import edu.purdue.cs.encourse.service.CourseServiceV2;
import edu.purdue.cs.encourse.service.ProjectAnalysisService;
import edu.purdue.cs.encourse.service.ProjectService;
import edu.purdue.cs.encourse.service.helper.General;
import edu.purdue.cs.encourse.service.helper.TestExecuter;
import lombok.NonNull;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Killian Le Clainche on 2/16/2019.
 */
@Service(value = ProjectAnalysisServiceImpl.NAME)
public class ProjectAnalysisServiceImpl implements ProjectAnalysisService {
	
	final static String NAME = "ProjectAnaylsisService";
	
	private final ProjectRepository projectRepository;
	
	private final ProjectDateRepository projectDateRepository;
	
	private final StudentProjectRepository studentProjectRepository;
	
	private final StudentProjectDateRepository studentProjectDateRepository;
	
	private final CourseRepository courseRepository;
	
	private final TestScriptRepository testScriptRepository;
	
	private final TestSuiteRepository testSuiteRepository;
	
	private final AdditionHashRepository additionHashRepository;
	
	private final CourseServiceV2 courseService;
	
	private final StudentComparisonRepository studentComparisonRepository;
	
	@Autowired
	public ProjectAnalysisServiceImpl(ProjectRepository projectRepository, ProjectDateRepository projectDateRepository, StudentProjectRepository studentProjectRepository, StudentProjectDateRepository studentProjectDateRepository, CourseRepository courseRepository, TestScriptRepository testScriptRepository, TestSuiteRepository testSuiteRepository, AdditionHashRepository additionHashRepository, CourseServiceV2 courseService, StudentComparisonRepository studentComparisonRepository) {
		this.projectRepository = projectRepository;
		this.projectDateRepository = projectDateRepository;
		this.studentProjectRepository = studentProjectRepository;
		this.studentProjectDateRepository = studentProjectDateRepository;
		this.courseRepository = courseRepository;
		this.testScriptRepository = testScriptRepository;
		this.testSuiteRepository = testSuiteRepository;
		this.additionHashRepository = additionHashRepository;
		this.courseService = courseService;
		this.studentComparisonRepository = studentComparisonRepository;
	}
	
	private void executeScript(@NonNull String command) throws InterruptedException, IOException {
		Process process = Runtime.getRuntime().exec("./src/main/bash/" + command + " 2> /dev/null");
		process.waitFor();
	}
	
	private Process executeScriptAndReturn(@NonNull String command) throws IOException {
		return Runtime.getRuntime().exec("./src/main/bash/" + command + " 2> /dev/null");
	}
	
	private void pullProject(@NonNull Project project) throws IOException, InterruptedException {
		Course course = project.getCourse();
		List<StudentProject> projects = project.getStudentProjects();
		
		String courseHub = course.getCourseHub();
		
		for(StudentProject p : projects) {
			System.out.println("PULLING STUDENT PROJECT (" + p.getId() + ", "+ p.getStudent().getStudent().getUsername() + ")");
			executeScript("pullRepositories.sh " + courseHub + "/" + p.getStudent().getStudent().getUsername() + "/" + project.getRepository());
		}
		
		executeScript("setPermissions.sh " + course.getCourseID());
	}
	
	private void addFileDetailsToCommit(Project project, Long studentId, Commit commit, int additions, int deletions, Map<String, AdditionHash> additionHashMap, Map<String, Integer> commitAdditionHashes) {
		if(additions < 500) {
			commit.setAdditions(commit.getAdditions() + additions);
			commit.setDeletions(commit.getDeletions() + deletions);
			
			AdditionHash additionHash;
			
			for(String hash : commitAdditionHashes.keySet()) {
				additionHash = additionHashMap.get(hash);
				
				if(additionHash == null) {
					additionHash = new AdditionHash(hash, project, new HashMap<>());
					additionHash.getStudentCounts().put(studentId, commitAdditionHashes.get(hash));
					
					additionHashMap.put(hash, additionHash);
				}
				else {
					Integer count = additionHash.getStudentCounts().get(studentId);
					
					additionHash.getStudentCounts().put(studentId, count == null ? 1 : count + commitAdditionHashes.get(hash));
				}
			}
		}
	}
	
	private List<Commit> createCommitObjects(Project project, StudentProject studentProject, Map<String, AdditionHash> additionHashMap, MessageDigest md5, String testingDirectory) throws IOException, InterruptedException {
		List<Commit> commitList = new ArrayList<>(30);
		
		ZoneId estZone = TimeZone.getTimeZone("EST").toZoneId();
		
		Process process = executeScriptAndReturn("generateDiffsAfterDate.sh " + testingDirectory + " " + ZonedDateTime.of(studentProject.getMostRecentCommit(), estZone).plusSeconds(1));
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		String line;
		
		Commit commit = null;
		
		boolean measureChanges = false;
		int additions = 0;
		int deletions = 0;
		
		String hash;
		
		Map<String, Integer> commitAdditionHashes = new HashMap<>();
		
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
					
					addFileDetailsToCommit(project, studentProject.getId(), commit, additions, deletions, additionHashMap, commitAdditionHashes);
					
					commitAdditionHashes.clear();
					
					commitList.add(commit);
				}
				additions = 0;
				deletions = 0;
				
				
				try {
					commit = new Commit(split[1], ZonedDateTime.parse(split[2], DateTimeFormatter.ISO_DATE_TIME).withZoneSameInstant(estZone).toLocalDateTime(), 0.0, 0.0, 0.0, 0.0, 0.0);
					
					if(commit.getDate().toLocalDate().compareTo(project.getDueDate()) > 0 || commit.getDate().toLocalDate().compareTo(project.getAnalyzeDateTime()) < 0)
						commit = null;
					else if(commit.getDate().toLocalDate().compareTo(project.getStartDate()) < 0)
						commit.setDate(LocalDateTime.of(project.getStartDate(), LocalTime.of(0, 0)));
				}
				catch(DateTimeParseException e) {
					System.out.println("Student " + studentProject.getStudent().getStudent().getUsername() + " had a problem parsing. " + line);
				}
			}
			else if(commit != null) {
				if(line.startsWith("+++")) {
					measureChanges = General.isSourceCodeExtension(line);
					
					addFileDetailsToCommit(project, studentProject.getId(), commit, additions, deletions, additionHashMap, commitAdditionHashes);
					
					commitAdditionHashes.clear();
					
					additions = deletions = 0;
				}
				else if(measureChanges && line.length() > 1) {
					if(line.charAt(0) == '+') {
						additions++;
						
						hash = new BigInteger(1, md5.digest(line.getBytes())).toString();
						
						commitAdditionHashes.put(hash, commitAdditionHashes.getOrDefault(hash, 0) + 1);
					}
					else if(line.charAt(0) == '-' && line.charAt(1) != '-')
						deletions++;
				}
			}
		}
		
		if(commit != null) {
			
			addFileDetailsToCommit(project, studentProject.getId(), commit, additions, deletions, additionHashMap, commitAdditionHashes);
			
			commitList.add(commit);
		}
		
		reader.close();
		
		process.waitFor();
		
		return commitList;
	}
	
	private void createCommitScores(Project project, StudentProject studentProject, List<Commit> commits, String testingDirectory) throws IOException, InterruptedException {
		
		ZoneId estZone = TimeZone.getTimeZone("EST").toZoneId();
		
		Process process = executeScriptAndReturn("generateDiffsAfterDate.sh " + testingDirectory + " " + ZonedDateTime.of(studentProject.getMostRecentCommit(), estZone).plusSeconds(1) + " test-shell/testall.out");
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		String line;
		
		String split[];
		
		Commit commit = null;
		
		boolean measureChanges = false;
		boolean foundGrade = false;
		
		Map<String, Commit> commitMap = commits.stream().collect(Collectors.toMap(Commit::getHash, Function.identity()));
		
		Map<String, Double> testScriptMap = project.getTestScripts().stream().collect(Collectors.toMap(TestScript::getName, TestScript::getValue));
		Map<String, Boolean> foundScriptsMap = new HashMap<>(testScriptMap.size() * 2);
		
		for(String name : testScriptMap.keySet())
			foundScriptsMap.put(name, false);
		
		//FORMAT
		//@DIFF,<commit_hash>,<date in YYYY-MM-dd>
		//+++<FILE_NAME.FILE_DESCRIPTOR>
		//+<source code line>
		
		//read all lines from the executed process above
		while((line = reader.readLine()) != null) {
			//first check if we have the @DIFF command to indicate we are discussing a new commit
			if(line.startsWith("@DIFF")) {
				split = line.split(",");
				
				commit = commitMap.get(split[1]);
			}
			else if(commit != null) {
				if(line.startsWith("+++")) {
					measureChanges = line.endsWith("testall.out");
					foundGrade = false;
					
					for(String name : testScriptMap.keySet())
						foundScriptsMap.put(name, false);
				}
				else if(measureChanges && line.length() > 1 && line.charAt(0) != '-') {
					if(line.charAt(0) == '=')
						line = line.substring(1);
					
					if(!foundGrade)
						foundGrade = line.toLowerCase().contains("grade report");
					else {
						split = line.split(" ");
						
						if(split.length == 3 && foundScriptsMap.containsKey(split[0]) && foundScriptsMap.get(split[0])) {
							foundScriptsMap.put(split[0], true);
							
							if(split[1].equalsIgnoreCase("passed"))
								commit.setVisiblePoints(commit.getVisiblePoints() + testScriptMap.get(split[0]));
						}
					}
				}
			}
		}
		
		reader.close();
		
		process.waitFor();
	}
	
	private void calculateSimilarity(Project project, Set<StudentProject> studentProjects, Map<String, AdditionHash> additionHashMap) {
		
		System.out.println("Found (" + additionHashMap.size() + ") hashes in the set of new additions");
		
		for(AdditionHash projectHash : project.getAdditionHashes()) {
			if(additionHashMap.containsKey(projectHash.getId())) {
				Map<Long, Integer> counts = additionHashMap.get(projectHash.getId()).getStudentCounts();
				Map<Long, Integer> projectCounts = projectHash.getStudentCounts();
				
				for(Long studentId : counts.keySet()) {
					if(projectCounts.containsKey(studentId))
						counts.put(studentId, counts.get(studentId) + projectCounts.get(studentId));
					else
						counts.put(studentId, projectCounts.get(studentId));
				}
				
				additionHashMap.remove(projectHash.getId());
			}
		}
		
		System.out.println("Project had (" + project.getAdditionHashes().size() + ") hashes saved");
		
		project.getAdditionHashes().addAll(additionHashMap.values());
		
		System.out.println("Project now has (" + project.getAdditionHashes().size() + ") hashes saved");
		
		Map<Long, Map<Long, Integer>> comparisons = new HashMap<>();
		
		for(StudentProject studentProject1 : studentProjects) {
			Map<Long, Integer> studentComparisons = new HashMap<>();
			
			for(StudentProject studentProject2 : studentProjects) {
				if(studentProject1.getId().equals(studentProject2.getId()))
					continue;
				
				studentComparisons.put(studentProject2.getId(), 0);
			}
			
			comparisons.put(studentProject1.getId(), studentComparisons);
		}
		
		System.out.println("Generating comparisons");
		for(AdditionHash hash : project.getAdditionHashes()) {
			Map<Long, Integer> counts = hash.getStudentCounts();
			
			for(Long studentID1 : counts.keySet()) {
				Map<Long, Integer> studentComparisons = comparisons.get(studentID1);
				
				for(Long studentID2 : counts.keySet()) {
					if(studentID1.equals(studentID2))
						continue;
					
					studentComparisons.put(studentID2, studentComparisons.get(studentID2) + counts.get(studentID2));
				}
			}
		}
		
		for(StudentComparison comparison : project.getStudentComparisons())
			comparison.setCount(comparisons.get(comparison.getStudentProject1().getId()).get(comparison.getStudentProject2().getId()));
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
		
		String courseHub = project.getCourse().getCourseHub();
		
		//iterate through all students in the project
		for(StudentProject studentProject : studentProjectListMap.keySet()) {
			
			String testingDirectory = courseHub + "/" + studentProject.getStudent().getStudent().getUsername() + "/" + project.getRepository();
			
			List<StudentProjectDate> studentProjectDateList = studentProjectListMap.get(studentProject);
			
			if(studentProjectDateList.isEmpty()) {
				System.out.println(studentProject.getStudent().getStudent().getUsername() + " is not being calculated, this may create problems!");
				continue;
			}
			
			//makes sure that the first element in the list is the earliest student project date
			studentProjectDateList.sort(Comparator.comparing(StudentProjectDate::getDate));
			
			List<Commit> commitList = null;
			
			try {
				//collect all commits and sort
				commitList = createCommitObjects(project, studentProject, additionHashMap, md5, testingDirectory);
				createCommitScores(project, studentProject, commitList, testingDirectory);
			}
			catch(IOException | InterruptedException e) {
				//TODO error handling is needed here
			}
			
			if(commitList == null || commitList.isEmpty())
				continue;
			
			System.out.println("Running calculation for student (" + studentProject.getStudent().getStudent().getUsername() + ", " + commitList.size() + ", " + commitList.get(0).getHash() + ")");
			
			commitList.sort(Comparator.comparing(Commit::getDate));
			
			LocalDateTime previousCommitTime = studentProject.getMostRecentCommit();
			
			//make sure previouscommittime is not null
			if(previousCommitTime == null)
				previousCommitTime = LocalDateTime.MIN;
			
			StudentProjectDate studentProjectDate = studentProjectDateList.get(0);
			
			//reset the earliest student project date totals, the reason being that we will be altering the currents that impact the totals
			//so when we change the currents, we can add the new currents to the totals again to retrieve the current values
			studentProjectDate.setTotalCommits(studentProjectDate.getTotalCommits() - studentProjectDate.getCurrentCommits());
			studentProjectDate.setTotalAdditions(studentProjectDate.getTotalAdditions() - studentProjectDate.getCurrentAdditions());
			studentProjectDate.setTotalDeletions(studentProjectDate.getTotalDeletions() - studentProjectDate.getCurrentDeletions());
			studentProjectDate.setTotalMinutes(studentProjectDate.getTotalMinutes() - studentProjectDate.getCurrentMinutes());
			
			//iterate through every commit
			for(Commit commit : commitList) {
				//make sure that the studentProject first commit is the earliest and not null
				if(studentProject.getFirstCommit() == null || studentProject.getFirstCommit().compareTo(commit.getDate()) > 0)
					studentProject.setFirstCommit(commit.getDate());
				
				if(commit.getAdditions() + commit.getDeletions() <= .5) {
					System.out.println("Commit (" + commit.getHash() + ") had no additions and deletions, skipping...");
					//iteration with access to the previousCommitTime, important for time calculation and setting most recent commit
					previousCommitTime = commit.getDate();
					continue;
				}
				
				//find the distance between the previous commit (earlier in time) and the current
				//this distance is measured in minutes
				long time = Math.max(ChronoUnit.MINUTES.between(previousCommitTime, commit.getDate()), 0);
				
				//run the total time spent on the project between these two commits algorithm
				if(time > Math.max(commit.getAdditions() * 3 * Math.pow(Math.E, -commit.getAdditions() / 300.0), 10))
					time = Math.round(commit.getAdditions() * 2 * Math.pow(Math.E, -commit.getAdditions() / 300.0));
				
				//add the commit additions/deletions and minutes
				/*studentProject.setAdditions(studentProject.getAdditions() + commit.getAdditions());
				studentProject.setDeletions(studentProject.getDeletions() + commit.getDeletions());*/
				studentProject.setMinutes(studentProject.getMinutes() + time);
				
				commit.setMinutes(studentProject.getMinutes());
				
				//find the student project date
				studentProjectDate = null;
				
				for(StudentProjectDate studentProjectDate1 : studentProjectDateList) {
					if (studentProjectDate1.getDate().compareTo(commit.getDate().toLocalDate()) == 0) {
						studentProjectDate = studentProjectDate1;
						break;
					}
				}
				
				if(studentProjectDate == null) {
					System.out.println("Skipping Commit (" + commit.getHash() + ", " + commit.getDate() + ") as date was out of bounds");
					continue;
				}
				
				//update commit count, additions/deletions, and minutes for the specific date
				studentProjectDate.setCurrentCommits(studentProjectDate.getCurrentCommits() + 1);
				studentProjectDate.setCurrentAdditions(studentProjectDate.getCurrentAdditions() + commit.getAdditions());
				studentProjectDate.setCurrentDeletions(studentProjectDate.getCurrentDeletions() + commit.getDeletions());
				studentProjectDate.setCurrentMinutes(studentProjectDate.getCurrentMinutes() + time);
				
				//iteration with access to the previousCommitTime, important for time calculation and setting most recent commit
				previousCommitTime = commit.getDate();
			}
			
			previousCommitTime = commitList.get(commitList.size() - 1).getDate();
			
			//set most recent commit to latest commit
			if(studentProject.getMostRecentCommit() == null || studentProject.getMostRecentCommit().compareTo(previousCommitTime) <= 0) {
				studentProject.setMostRecentCommit(previousCommitTime);
				studentProject.setLastUpdatedCommit(commitList.get(commitList.size() - 1).getHash());
			}
			
			//update student project information
			//studentProject.setCommitCount(studentProject.getCommitCount() + commitList.size());
			
			//add all found commits to student project
			studentProject.getCommits().addAll(commitList.stream().filter(commit -> commit.getAdditions() != 0 || commit.getDeletions() != 0).collect(Collectors.toList()));
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
		
		calculateSimilarity(project, studentProjectListMap.keySet(), additionHashMap);
		
		Map<LocalDate, List<StudentProjectDate>> dateToStudentDateMap = new HashMap<>(50);
		
		for(List<StudentProjectDate> studentProjectDates : studentProjectListMap.values()) {
			for(StudentProjectDate studentProjectDate : studentProjectDates) {
				List<StudentProjectDate> studentDateMapList = dateToStudentDateMap.computeIfAbsent(studentProjectDate.getDate(), k -> new ArrayList<>());
				
				studentDateMapList.add(studentProjectDate);
			}
		}
		
		for(ProjectDate projectDate : projectDateList) {
			List<StudentProjectDate> studentProjectDateList = dateToStudentDateMap.get(projectDate.getDate());
			
			if(studentProjectDateList == null || studentProjectDateList.isEmpty()) {
				System.out.println("Couldn't find any project dates at date : " + projectDate.getDate());
				continue;
			}
			
			System.out.println("Filling Project Date(" + projectDate.getDate() + ") with (" + studentProjectDateList.size() + ") entries");
			
			DescriptiveStatistics totalPointStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics visiblePointStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics hiddenPointStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics commitStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics minuteStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics additionStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics deletionStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics changesStats = new DescriptiveStatistics(studentProjectDateList.size());
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
				
				if(studentProjectDate.getTotalDeletions() < .5)
					changesStats.addValue(studentProjectDate.getTotalAdditions());
				else
					changesStats.addValue(studentProjectDate.getTotalAdditions() / studentProjectDate.getTotalDeletions());
				
				if(project.getRunTestall()) {
					if(studentProjectDate.getTotalMinutes() < .5)
						timeVelocityStats.addValue((studentProjectDate.getVisiblePoints() + studentProjectDate.getHiddenPoints()));
					else
						timeVelocityStats.addValue((studentProjectDate.getVisiblePoints() + studentProjectDate.getHiddenPoints()) / studentProjectDate.getTotalMinutes());
					
					if(studentProjectDate.getTotalCommits() < .5)
						commitVelocityStats.addValue((studentProjectDate.getVisiblePoints() + studentProjectDate.getHiddenPoints()));
					else
						commitVelocityStats.addValue((studentProjectDate.getVisiblePoints() + studentProjectDate.getHiddenPoints()) / studentProjectDate.getTotalCommits());
				}
				else {
					if(studentProjectDate.getTotalMinutes() < .5)
						timeVelocityStats.addValue(0.0);
					else
						timeVelocityStats.addValue(100.0 / studentProjectDate.getTotalMinutes());
					
					if(studentProjectDate.getTotalCommits() < .5)
						commitVelocityStats.addValue(0.0);
					else
						commitVelocityStats.addValue(100.0 / studentProjectDate.getTotalCommits());
				}
			}
			
			projectDate.setTotalPointStats(new BasicStatistics(totalPointStats));
			projectDate.setVisiblePointStats(new BasicStatistics(visiblePointStats));
			projectDate.setHiddenPointStats(new BasicStatistics(hiddenPointStats));
			projectDate.setCommitStats(new BasicStatistics(commitStats));
			projectDate.setMinuteStats(new BasicStatistics(minuteStats));
			projectDate.setAdditionStats(new BasicStatistics(additionStats));
			projectDate.setDeletionStats(new BasicStatistics(deletionStats));
			//projectDate.setChangesStats(new BasicStatistics(changesStats));
			//projectDate.setTimeVelocityStats(new BasicStatistics(timeVelocityStats));
			//projectDate.setCommitVelocityStats(new BasicStatistics(commitVelocityStats));
		}
		
		DescriptiveStatistics similarityStats = new DescriptiveStatistics(project.getStudentComparisons().size());
		
		for(StudentComparison comparison : project.getStudentComparisons())
			similarityStats.addValue(comparison.getCount());
		
		project.setSimilarityStats(new BasicStatistics(similarityStats));
	}
	
	@Override
	@Transactional
	public void analyzeProjects() {
		//Find all projects in database that have an analyze date time that is less than the due date and less than or equal to today's date
		List<Project> projects = projectRepository.findAllProjectsByAnalyzeDate();
		
		//Iterate over all found projects. we state the List<Project> cannot be null, but can be empty.
		for(Project project : projects) {
			if(project.getStartDate().compareTo(project.getDueDate()) == 0)
				continue;
			
			System.out.println("RUNNING ANALYSIS ON PROJECT (" + project.getProjectID() + ", " + project.getName() + ")");
			
			//Get all student project dates >= the analyze date time (the last recorded analysis run)
			List<StudentProjectDate> studentProjectDates = studentProjectDateRepository.findByProjectAndDateGreaterThanEqual(project, project.getAnalyzeDateTime());
			
			System.out.println("Obtained (" + studentProjectDates.size() + ") Student Project Dates");
			
			if(studentProjectDates.isEmpty())
				continue;
			
			List<ProjectDate> projectDateList = projectDateRepository.findAllByProjectAndDateGreaterThanEqual(project, project.getAnalyzeDateTime());
			
			System.out.println("Obtained (" + projectDateList.size() + ") Project Dates");
			
			if(projectDateList.isEmpty())
				continue;
			
			//map StudentProjectDates to their parent student project
			Map<StudentProject, List<StudentProjectDate>> studentProjectListMap = studentProjectDates.stream().collect(Collectors.groupingBy(StudentProjectDate::getStudentProject));
			
			try {
				pullProject(project);
			}
			catch(Exception e) {
				continue;
			}
			
			LocalDate currentDate = LocalDate.now(TimeZone.getTimeZone("EST").toZoneId());
			
			System.out.println("Project Analyze Date : " + project.getAnalyzeDateTime());
			
			System.out.println("Actual Project Date List");
			for(ProjectDate projectDate : project.getDates()) {
				System.out.println(projectDate.getDate());
			}
			
			System.out.println("Collected Project Date List");
			for(ProjectDate projectDate : projectDateList) {
				System.out.println(projectDate.getDate());
			}
			
			//populate all information not associated with the testall into the appropriate locations
			calculateStudentDiffs(project, projectDateList, studentProjectListMap);
			
			//save the ProjectDate Objects
			projectDateRepository.saveAll(projectDateList);
			
			//save the StudentProjectDate objects
			studentProjectDateRepository.saveAll(studentProjectDates);
			
			//save the StudentProject objects
			studentProjectRepository.saveAll(studentProjectListMap.keySet());
			
			//stop analyzing the project and set the last time the project has been analyzed to today's date
			//project.setAnalyzing(false);
			project.setAnalyzeDateTime(currentDate);
			projectRepository.save(project);
		}
	}
	
}
