package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.ProjectDateRepository;
import edu.purdue.cs.encourse.database.ProjectRepository;
import edu.purdue.cs.encourse.database.StudentProjectDateRepository;
import edu.purdue.cs.encourse.database.StudentProjectRepository;
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
import edu.purdue.cs.encourse.service.ProjectAnalysisService;
import edu.purdue.cs.encourse.service.helper.General;
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
	
	@Autowired
	public ProjectAnalysisServiceImpl(ProjectRepository projectRepository, ProjectDateRepository projectDateRepository, StudentProjectRepository studentProjectRepository, StudentProjectDateRepository studentProjectDateRepository) {
		this.projectRepository = projectRepository;
		this.projectDateRepository = projectDateRepository;
		this.studentProjectRepository = studentProjectRepository;
		this.studentProjectDateRepository = studentProjectDateRepository;
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
			
			if(!new File(courseHub + "/" + p.getStudent().getStudent().getUsername() + "/" + project.getRepository()).isDirectory())
				continue;
			
			System.out.println("PULLING STUDENT PROJECT (" + p.getId() + ", "+ p.getStudent().getStudent().getUsername() + ")");
			executeScript("pullRepositories.sh " + courseHub + " " + p.getStudent().getStudent().getUsername() + "/" + project.getRepository());
		}
		
		executeScript("setPermissions.sh " + course.getCourseID());
	}
	
	private void addFileDetailsToCommit(Project project, Long studentId, Commit commit, boolean isStudentCode, int additions, int deletions, Map<String, AdditionHash> additionHashMap, Map<String, Integer> commitAdditionHashes) {
		if(additions < 500) {
			commit.setAdditions(commit.getAdditions() + additions);
			commit.setDeletions(commit.getDeletions() + deletions);
			
			AdditionHash additionHash;
			
			for(String hash : commitAdditionHashes.keySet()) {
				additionHash = additionHashMap.get(hash);
				
				if(additionHash == null) {
					additionHash = new AdditionHash(hash, project, isStudentCode, new HashMap<>());
					
					if(additionHash.getIsStudentCode())
						additionHash.getStudentCounts().put(studentId, commitAdditionHashes.get(hash));
					
					additionHashMap.put(hash, additionHash);
				}
				else {
					additionHash.setIsStudentCode(additionHash.getIsStudentCode() && isStudentCode);
					
					if(additionHash.getIsStudentCode())
						additionHash.getStudentCounts().put(studentId, additionHash.getStudentCounts().getOrDefault(studentId, 0) + commitAdditionHashes.get(hash));
					else
						additionHash.getStudentCounts().clear();
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
		
		boolean isStudentCode = true;
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
					
					addFileDetailsToCommit(project, studentProject.getId(), commit, isStudentCode, additions, deletions, additionHashMap, commitAdditionHashes);
					
					commitAdditionHashes.clear();
					
					if(isStudentCode)
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
				
				isStudentCode = !project.getIgnoredUsers().contains(split[3]);
			}
			else if(commit != null) {
				if(line.startsWith("+++")) {
					measureChanges = General.isSourceCodeExtension(line);
					
					addFileDetailsToCommit(project, studentProject.getId(), commit, isStudentCode, additions, deletions, additionHashMap, commitAdditionHashes);
					
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
			
			addFileDetailsToCommit(project, studentProject.getId(), commit, isStudentCode, additions, deletions, additionHashMap, commitAdditionHashes);
			
			if(isStudentCode)
				commitList.add(commit);
		}
		
		reader.close();
		
		process.waitFor();
		
		return commitList;
	}
	
	private void createCommitScores(Project project, StudentProject studentProject, List<StudentProjectDate> studentProjectDates, List<Commit> commits, String testingDirectory) throws IOException, InterruptedException {
		
		ZoneId estZone = TimeZone.getTimeZone("EST").toZoneId();
		
		Process process = executeScriptAndReturn("getTestallChanges.sh " + testingDirectory + " " + ZonedDateTime.of(studentProject.getMostRecentCommit(), estZone).plusSeconds(1) + " test-shell/testall.out");
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		String line;
		
		String split[];
		
		Commit commit = null;
		
		boolean measureChanges = false;
		boolean foundGrade = false;
		
		Map<String, Commit> commitMap = commits.stream().collect(Collectors.toMap(Commit::getHash, Function.identity()));
		
		Map<String, TestScript> testScriptMap = project.getTestScripts().stream().collect(Collectors.toMap(TestScript::getName, Function.identity()));
		Map<String, Boolean> foundScriptsMap = new HashMap<>(testScriptMap.size() * 2);
		
		for(String name : testScriptMap.keySet())
			foundScriptsMap.put(name, false);
		
		Map<LocalDate, StudentProjectDate> studentProjectDateMap = studentProjectDates.stream().collect(Collectors.toMap(StudentProjectDate::getDate, Function.identity()));
		
		//FORMAT
		//@DIFF,<commit_hash>,<date in YYYY-MM-dd>
		//+++<FILE_NAME.FILE_DESCRIPTOR>
		//+<source code line>
		
		//read all lines from the executed process above
		while((line = reader.readLine()) != null) {
			//first check if we have the @DIFF command to indicate we are discussing a new commit
			if(line.startsWith("@DIFF")) {
				split = line.split(",");
				
				if(commit != null && foundGrade) {
					if(studentProjectDateMap.containsKey(commit.getDate().toLocalDate())) {
						StudentProjectDate studentProjectDate = studentProjectDateMap.get(commit.getDate().toLocalDate());
						
						if(studentProjectDate.getVisiblePoints() < commit.getVisiblePoints() - .05) {
							studentProjectDate.setVisiblePoints(commit.getVisiblePoints());
							studentProjectDate.getTestsPassing().clear();
							
							for(String name : testScriptMap.keySet()) {
								if(foundScriptsMap.containsKey(name) && foundScriptsMap.get(name))
									studentProjectDate.getTestsPassing().add(testScriptMap.get(name).getId());
							}
						}
						
						if(studentProject.getVisiblePoints() + .05 < commit.getVisiblePoints()) {
							studentProject.setVisiblePoints(commit.getVisiblePoints());
							studentProject.getTestsPassing().clear();
							
							for(String name : testScriptMap.keySet()) {
								if(foundScriptsMap.containsKey(name) && foundScriptsMap.get(name))
									studentProject.getTestsPassing().add(testScriptMap.get(name).getId());
							}
						}
					}
					else
						System.out.println("Commit (" + commit.getHash() + ", " + commit.getDate() + ") for Student (" + studentProject.getStudent().getStudent().getUsername() + ") was not found in list of dates");
				}
				
				commit = commitMap.get(split[1]);
				foundGrade = measureChanges = false;
				
				for(String name : testScriptMap.keySet())
					foundScriptsMap.put(name, false);
				
				if(project.getIgnoredUsers().contains(split[3]))
					commit = null;
			}
			else if(commit != null) {
				if(line.startsWith("+++")) {
					measureChanges = line.endsWith("testall.out");
					foundGrade = false;
				}
				else if(measureChanges && line.length() > 1 && line.charAt(0) != '-') {
					if(line.charAt(0) == '=')
						line = line.substring(1);
					
					if(!foundGrade)
						foundGrade = line.toLowerCase().contains("grade report");
					else {
						split = line.split(" ");
						
						if(split.length == 3 && testScriptMap.containsKey(split[0]) && foundScriptsMap.containsKey(split[0]) && !foundScriptsMap.get(split[0])) {
							foundScriptsMap.put(split[0], true);
							
							if(split[1].equalsIgnoreCase("passed"))
								commit.setVisiblePoints(commit.getVisiblePoints() + testScriptMap.get(split[0]).getValue());
						}
						else {
							System.out.println("Problem with line " + line);
						}
					}
				}
			}
		}
		
		if(commit != null && foundGrade) {
			if(!studentProjectDateMap.containsKey(commit.getDate().toLocalDate()))
				System.out.println("Commit (" + commit.getHash() + ", " + commit.getDate() + ") for Student (" + studentProject.getStudent().getStudent().getUsername() + ") was not found in list of dates");
			else {
				StudentProjectDate studentProjectDate = studentProjectDateMap.get(commit.getDate().toLocalDate());
				
				if(commit.getVisiblePoints() > studentProjectDate.getVisiblePoints() + .05) {
					studentProjectDate.setVisiblePoints(commit.getVisiblePoints());
					studentProjectDate.getTestsPassing().clear();
					
					for(String name : testScriptMap.keySet()) {
						if(foundScriptsMap.containsKey(name) && foundScriptsMap.get(name))
							studentProjectDate.getTestsPassing().add(testScriptMap.get(name).getId());
					}
				}
				
				if(commit.getVisiblePoints() - .05 > studentProject.getVisiblePoints()) {
					studentProject.setVisiblePoints(commit.getVisiblePoints());
					studentProject.getTestsPassing().clear();
					
					for(String name : testScriptMap.keySet()) {
						if(foundScriptsMap.containsKey(name) && foundScriptsMap.get(name))
							studentProject.getTestsPassing().add(testScriptMap.get(name).getId());
					}
				}
			}
		}
		
		reader.close();
		
		process.waitFor();
	}
	
	private void calculateSimilarity(Project project, Set<StudentProject> studentProjects, Map<String, AdditionHash> additionHashMap) {
		System.out.println("Found (" + additionHashMap.size() + ") hashes in the set of new additions");
		
		//iterate over all old hashes found
		for(AdditionHash projectHash : project.getAdditionHashes()) {
			//check if new hashes contains old hash
			if(additionHashMap.containsKey(projectHash.getId())) {
				//get hash mapping (student -> count) from new and old hashes
				Map<Long, Integer> counts = additionHashMap.get(projectHash.getId()).getStudentCounts();
				Map<Long, Integer> projectCounts = projectHash.getStudentCounts();
				
				//iterate over all students in new hash mapping
				for(Long studentID : counts.keySet()) {
					//put into project hashes the sum of both, if project counts doesn't have that student, default to zero
					projectCounts.put(studentID, counts.get(studentID) + projectCounts.getOrDefault(studentID, 0));
				}
				
				//remove from new hashes so we don't readd
				additionHashMap.remove(projectHash.getId());
			}
		}
		
		System.out.println("Project had (" + project.getAdditionHashes().size() + ") hashes saved");
		
		project.getAdditionHashes().addAll(additionHashMap.values());
		
		System.out.println("Project now has (" + project.getAdditionHashes().size() + ") hashes saved");
		
		//generate the comparisons mapping and set the values to be zeroed
		Map<Long, Map<Long, Integer>> studentComparisons = new HashMap<>();
		Map<Long, StudentProject> studentProjectMap = studentProjects.stream().collect(Collectors.toMap(StudentProject::getId, Function.identity()));
		
		for(StudentProject studentProject1 : studentProjects) {
			Map<Long, Integer> studentComparisonsCount = new HashMap<>();
			
			for(StudentProject studentProject2 : studentProjects) {
				if(studentProject1.getId().equals(studentProject2.getId()))
					continue;
				
				studentComparisonsCount.put(studentProject2.getId(), 0);
			}
			
			studentComparisons.put(studentProject1.getId(), studentComparisonsCount);
		}
		
		System.out.println("Generating comparisons");
		//iterate over old + new hashes
		for(AdditionHash hash : project.getAdditionHashes()) {
			Map<Long, Integer> counts = hash.getStudentCounts();
			
			if(counts.size() > Math.max(75, Math.floor(studentProjects.size() * .33)))
				continue;
			
			if(counts.size() > Math.max(33, Math.floor(studentProjects.size() * .16))) {
				int total = 0;
				
				for(Long studentID : counts.keySet())
					total += counts.get(studentID);
				
				if(total / counts.size() >= 3)
					continue;
			}
			
			//iterate over the students who share the hash
			for(Long studentID1 : counts.keySet()) {
				//get studentID1's mapping towards all other students (comparisons)
				Map<Long, Integer> comparisons = studentComparisons.get(studentID1);
				
				if(!studentProjectMap.containsKey(studentID1) || counts.get(studentID1) + .05 >= studentProjectMap.get(studentID1).getAdditions() * .02)
					continue;
				
				//iterate over the students who share the hash, skip over the current student (studentID1)
				for(Long studentID2 : counts.keySet()) {
					if(studentID1.equals(studentID2))
						continue;
					
					if(!studentProjectMap.containsKey(studentID2) || counts.get(studentID2) + .0005 >= studentProjectMap.get(studentID2).getAdditions() * .02)
						continue;
					
					//set studentID1's value for key studentID2 to be the summation of studentID2's counts for the specific hash
					//as well as the current studentID2 value
					comparisons.put(studentID2, 1 + comparisons.get(studentID2));
				}
			}
		}
		
		//iterate over all comparisons and generate values
		for(StudentComparison comparison : project.getStudentComparisons()) {
			StudentProject studentProject1 = comparison.getStudentProject1();
			StudentProject studentProject2 = comparison.getStudentProject2();
			Integer comparisonCount = studentComparisons.get(studentProject2.getId()).get(studentProject1.getId());
			
			//count is the summation between the two students similarities
			//percent is the max function between each students (similarity count / additions)
			comparison.setCount(comparisonCount);
			
			double percent1 = studentProject1.getAdditions() < .05 ? 0.0 : (comparisonCount * 100.0) / studentProject1.getAdditions();
			double percent2 = studentProject2.getAdditions() < .05 ? 0.0 : (comparisonCount * 100.0) / studentProject2.getAdditions();
			
			comparison.setPercent(Math.max(percent1, percent2));
		}
		
		//iterate over the student projects for the project
		for(StudentProject studentProject : studentProjects) {
			studentProject.setCountSimilarity(0);
			studentProject.setCountPercentSimilarity(0.0);
			studentProject.setPercentSimilarity(0.0);
			studentProject.setPercentCountSimilarity(0);
		}
		
		for(StudentComparison comparison : project.getStudentComparisons()) {
			StudentProject studentProject = comparison.getStudentProject1();
			
			if(comparison.getStudentProject2().getAdditions() > 49.95) {
				if(studentProject.getCountSimilarity() < comparison.getCount()) {
					studentProject.setCountSimilarity(comparison.getCount());
					studentProject.setCountPercentSimilarity(comparison.getPercent());
				}
				
				if(studentProject.getPercentSimilarity() < comparison.getPercent()) {
					studentProject.setPercentSimilarity(comparison.getPercent());
					studentProject.setPercentCountSimilarity(comparison.getCount());
				}
			}
			
			studentProject = comparison.getStudentProject2();
			
			if(comparison.getStudentProject1().getAdditions() > 49.95) {
				if(studentProject.getCountSimilarity() < comparison.getCount()) {
					studentProject.setCountSimilarity(comparison.getCount());
					studentProject.setCountPercentSimilarity(comparison.getPercent());
				}
				
				if(studentProject.getPercentSimilarity() < comparison.getPercent()) {
					studentProject.setPercentSimilarity(comparison.getPercent());
					studentProject.setPercentCountSimilarity(comparison.getCount());
				}
			}
		}
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
			
			if(!new File(testingDirectory).isDirectory())
				continue;
			
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
				if(project.getRunTestall())
					createCommitScores(project, studentProject, studentProjectDateList, commitList, testingDirectory);
			}
			catch(IOException | InterruptedException e) {
				//TODO error handling is needed here
				e.printStackTrace();
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
			studentProjectDate.setTotalSeconds(studentProjectDate.getTotalSeconds() - studentProjectDate.getCurrentSeconds());
			
			//iterate through every commit
			for(Commit commit : commitList) {
				//make sure that the studentProject first commit is the earliest and not null
				if(studentProject.getFirstCommit() == null || studentProject.getFirstCommit().compareTo(commit.getDate()) > 0)
					studentProject.setFirstCommit(commit.getDate());
				
				if(commit.getAdditions() + commit.getDeletions() <= .05) {
					System.out.println("Commit (" + commit.getHash() + ") had no additions and deletions, skipping...");
					//iteration with access to the previousCommitTime, important for time calculation and setting most recent commit
					previousCommitTime = commit.getDate();
					continue;
				}
				
				//find the distance between the previous commit (earlier in time) and the current
				//this distance is measured in seconds
				long time = Math.max(ChronoUnit.SECONDS.between(previousCommitTime, commit.getDate()), 0);
				
				//run the total time spent on the project between these two commits algorithm
				if(time > 60 * Math.max(commit.getAdditions() * 3 * Math.pow(Math.E, -commit.getAdditions() / 300.0), 10))
					time = 60 * Math.round(commit.getAdditions() * 2 * Math.pow(Math.E, -commit.getAdditions() / 300.0));
				
				//add the commit additions/deletions and seconds
				studentProject.setSeconds(studentProject.getSeconds() + time);
				
				commit.setSeconds(studentProject.getSeconds());
				
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
				
				//update commit count, additions/deletions, and seconds for the specific date
				studentProjectDate.setCurrentCommits(studentProjectDate.getCurrentCommits() + 1);
				studentProjectDate.setCurrentAdditions(studentProjectDate.getCurrentAdditions() + commit.getAdditions());
				studentProjectDate.setCurrentDeletions(studentProjectDate.getCurrentDeletions() + commit.getDeletions());
				studentProjectDate.setCurrentSeconds(studentProjectDate.getCurrentSeconds() + time);
				
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
				studentProjectDate.setTotalSeconds(previousStudentDate.getTotalSeconds() + studentProjectDate.getCurrentSeconds());
				studentProjectDate.setTotalAdditions(previousStudentDate.getTotalAdditions() + studentProjectDate.getCurrentAdditions());
				studentProjectDate.setTotalDeletions(previousStudentDate.getTotalDeletions() + studentProjectDate.getCurrentDeletions());
				
				previousStudentDate = studentProjectDate;
			}
			
			if(previousStudentDate != null) {
				studentProject.setCommitCount(previousStudentDate.getTotalCommits());
				studentProject.setAdditions(previousStudentDate.getTotalAdditions());
				studentProject.setDeletions(previousStudentDate.getTotalDeletions());
				studentProject.setSeconds(previousStudentDate.getTotalSeconds());
				
				if(previousStudentDate.getTotalDeletions() < .05)
					studentProject.setChanges(previousStudentDate.getTotalAdditions());
				else
					studentProject.setChanges(previousStudentDate.getTotalAdditions() / previousStudentDate.getTotalDeletions());
				
				if(project.getRunTestall()) {
					if(studentProject.getSeconds() < .05)
						studentProject.setTimeVelocity((studentProject.getVisiblePoints() + studentProject.getHiddenPoints()));
					else
						studentProject.setTimeVelocity((studentProject.getVisiblePoints() + studentProject.getHiddenPoints()) / (studentProject.getSeconds() / 60.0));
					
					if(previousStudentDate.getTotalCommits() < .05)
						studentProject.setCommitVelocity((studentProject.getVisiblePoints() + studentProject.getHiddenPoints()));
					else
						studentProject.setCommitVelocity((studentProject.getVisiblePoints() + studentProject.getHiddenPoints()) / previousStudentDate.getTotalCommits());
				}
				else {
					if(studentProject.getSeconds() < 10.05)
						studentProject.setTimeVelocity(0.0);
					else
						studentProject.setTimeVelocity(6000.0 / studentProject.getSeconds());
					
					if(previousStudentDate.getTotalCommits() < 10.05)
						studentProject.setCommitVelocity(0.0);
					else
						studentProject.setCommitVelocity(100.0 / previousStudentDate.getTotalCommits());
				}
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
		
		int validCount;
		
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
			DescriptiveStatistics secondStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics additionStats = new DescriptiveStatistics(studentProjectDateList.size());
			DescriptiveStatistics deletionStats = new DescriptiveStatistics(studentProjectDateList.size());
			
			validCount = 0;
			
			for(StudentProjectDate studentProjectDate : studentProjectDateList) {
				if(studentProjectDate.getTotalCommits() < .05)
					continue;
				
				totalPointStats.addValue(studentProjectDate.getVisiblePoints() + studentProjectDate.getHiddenPoints());
				visiblePointStats.addValue(studentProjectDate.getVisiblePoints());
				hiddenPointStats.addValue(studentProjectDate.getHiddenPoints());
				commitStats.addValue(studentProjectDate.getTotalCommits());
				secondStats.addValue(studentProjectDate.getTotalSeconds());
				additionStats.addValue(studentProjectDate.getTotalAdditions());
				deletionStats.addValue(studentProjectDate.getTotalDeletions());
				
				validCount++;
			}
			
			projectDate.setValidCount(validCount);
			
			projectDate.setTotalPointStats(new BasicStatistics(totalPointStats));
			projectDate.setVisiblePointStats(new BasicStatistics(visiblePointStats));
			projectDate.setHiddenPointStats(new BasicStatistics(hiddenPointStats));
			projectDate.setCommitStats(new BasicStatistics(commitStats));
			projectDate.setSecondStats(new BasicStatistics(secondStats));
			projectDate.setAdditionStats(new BasicStatistics(additionStats));
			projectDate.setDeletionStats(new BasicStatistics(deletionStats));
		}
		
		DescriptiveStatistics changesStats = new DescriptiveStatistics(studentProjectListMap.size());
		DescriptiveStatistics timeVelocityStats = new DescriptiveStatistics(studentProjectListMap.size());
		DescriptiveStatistics commitVelocityStats = new DescriptiveStatistics(studentProjectListMap.size());
		
		validCount = 0;
		
		for(StudentProject studentProject : studentProjectListMap.keySet()) {
			if(studentProject.getCommitCount() < .05 || studentProject.getAdditions() < 49.95)
				continue;
			
			changesStats.addValue(studentProject.getChanges());
			timeVelocityStats.addValue(studentProject.getTimeVelocity());
			commitVelocityStats.addValue(studentProject.getCommitVelocity());
			
			validCount++;
		}
		
		project.setValidCount(validCount);
		
		project.setChangesStats(new BasicStatistics(changesStats));
		project.setTimeVelocityStats(new BasicStatistics(timeVelocityStats));
		project.setCommitVelocityStats(new BasicStatistics(commitVelocityStats));
		
		DescriptiveStatistics similarityStats = new DescriptiveStatistics(project.getStudentComparisons().size());
		DescriptiveStatistics similarityPercentStats = new DescriptiveStatistics(project.getStudentComparisons().size());
		
		validCount = 0;
		
		for(StudentComparison comparison : project.getStudentComparisons()) {
			if(comparison.getStudentProject1().getCommitCount() < .05 || comparison.getStudentProject2().getCommitCount() < .05)
				continue;
			
			if(comparison.getStudentProject1().getAdditions() < 49.95 || comparison.getStudentProject2().getAdditions() < 49.95)
				continue;
			
			similarityStats.addValue(comparison.getCount());
			similarityPercentStats.addValue(comparison.getPercent());
			
			validCount++;
		}
		
		project.setValidSimilarityCount(validCount);
		
		project.setSimilarityStats(new BasicStatistics(similarityStats));
		project.setSimilarityPercentStats(new BasicStatistics(similarityPercentStats));
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
