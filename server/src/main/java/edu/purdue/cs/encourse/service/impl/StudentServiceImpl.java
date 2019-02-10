package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.CourseStudentRepository;
import edu.purdue.cs.encourse.database.StudentProjectDateRepository;
import edu.purdue.cs.encourse.database.StudentProjectRepository;
import edu.purdue.cs.encourse.domain.Commit;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.ProjectDate;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.TestScript;
import edu.purdue.cs.encourse.domain.TestSuite;
import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.FrequencyDate;
import edu.purdue.cs.encourse.model.ProjectStudentSearchModel;
import edu.purdue.cs.encourse.model.StudentInfoModel;
import edu.purdue.cs.encourse.model.SearchModel;
import edu.purdue.cs.encourse.model.StudentTestSuiteInfoModel;
import edu.purdue.cs.encourse.model.student.StudentProjectDiffs;
import edu.purdue.cs.encourse.model.student.StudentTestSuiteInfo;
import edu.purdue.cs.encourse.service.StudentService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.InvalidRelationIdException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
@Service(value = StudentServiceImpl.NAME)
public class StudentServiceImpl implements StudentService {
	
	final static String NAME = "StudentService";
	
	@Autowired
	private CourseStudentRepository courseStudentRepository;
	
	private final StudentProjectRepository studentProjectRepository;
	
	@Autowired
	private StudentProjectDateRepository studentProjectDateRepository;
	
	@Autowired
	public StudentServiceImpl(StudentProjectRepository studentProjectRepository) {
		this.studentProjectRepository = studentProjectRepository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public CourseStudent getStudent(@NonNull Long studentID) throws InvalidRelationIdException {
		Optional<CourseStudent> studentOptional = courseStudentRepository.findById(studentID);
		
		if(!studentOptional.isPresent())
			throw new InvalidRelationIdException("Student ID (" + studentID + ") does not exist in the database.");
		
		return studentOptional.get();
	}
	
	@Override
	@Transactional(readOnly = true)
	public StudentProject getStudentProject(@NonNull Long projectID, @NonNull Long studentID) throws InvalidRelationIdException {
		Optional<StudentProject> studentProjectOptional = studentProjectRepository.findByProject_ProjectIDAndStudent_Id(projectID, studentID);
		
		if(!studentProjectOptional.isPresent())
			throw new InvalidRelationIdException("Project ID (" + projectID + ") with Student ID (" + studentID + ") does not exist in the database.");
		
		return studentProjectOptional.get();
	}
	
	@Override
	@Transactional(readOnly = true)
	public StudentProjectDate getStudentProjectDate(@NonNull StudentProject studentProject, LocalDate date) throws InvalidRelationIdException {
		date = date == null ? studentProject.getMostRecentCommit().toLocalDate() : date;
		
		Optional<StudentProjectDate> studentProjectDateOptional = studentProjectDateRepository.findByStudentProjectAndDateEquals(studentProject, date);
		
		if(!studentProjectDateOptional.isPresent())
			throw new InvalidRelationIdException("Student Project ID (" + studentProject.getId() + ") and Date (" + date + ") does not exist in the database.");
		
		return studentProjectDateOptional.get();
	}
	
	@Override
	@Transactional(readOnly = true)
	public StudentProjectDate getStudentProjectDate(@NonNull Long projectID, @NonNull Long studentID, LocalDate date) throws InvalidRelationIdException {
		StudentProject studentProject = getStudentProject(projectID, studentID);
		
		date = date == null ? studentProject.getMostRecentCommit().toLocalDate() : date;
		
		Optional<StudentProjectDate> studentProjectDateOptional = studentProjectDateRepository.findByStudentProjectAndDateEquals(studentProject, date);
		
		if(!studentProjectDateOptional.isPresent())
			throw new InvalidRelationIdException("Project ID (" + projectID + ") with Student ID (" + studentID + ") and Date (" + date + ") does not exist in the database.");
		
		return studentProjectDateOptional.get();
	}
	
	/*@Override
	public StudentProjectInfo getStudentProjectInfo(Project project, StudentProject studentProject, boolean includeTests) {
		StudentProjectInfo studentProjectInfo = new StudentProjectInfo();
		
		studentProjectInfo.setStudentProjectID(studentProject.getId());
		
		studentProjectInfo.setAdditions(Math.round(studentProject.getAdditions()));
		studentProjectInfo.setDeletions(Math.round(studentProject.getDeletions()));
		
		studentProjectInfo.setCommits(Math.round(studentProject.getCommitCount()));
		
		studentProjectInfo.setMinutes(Math.round(studentProject.getMinutes()));
		
		studentProjectInfo.setVisiblePoints(Math.round(studentProject.getVisiblePoints()));
		
		studentProjectInfo.setHiddenPoints(Math.round(studentProject.getHiddenPoints()));
		
		if(includeTests) {
			List<TestSuite> testSuites = project.getTestSuites();
			List<StudentTestSuiteInfo> testSuiteInfos = new ArrayList<>();
			
			for (TestSuite testSuite : testSuites) {
				StudentTestSuiteInfo testSuiteInfo = new StudentTestSuiteInfo();
				
				testSuiteInfo.name = testSuite.getName();
				testSuiteInfo.total = 0L;
				testSuiteInfo.current = 0L;
				
				for (TestScript testScript : testSuite.getTestScripts()) {
					Long value = Math.round(testScript.getValue());
					
					testSuiteInfo.total += value;
					
					if(studentProject.getTestsPassing().contains(testScript.getId()))
						testSuiteInfo.current += value;
				}
				
				if(testSuiteInfo.total > 0L)
					testSuiteInfos.add(testSuiteInfo);
			}
			
			if(!testSuiteInfos.isEmpty())
				studentProjectInfo.setStudentTestSuites(testSuiteInfos);
		}
		
		return studentProjectInfo;
	}*/
	
	@Override
	@Transactional(readOnly = true)
	public StudentInfoModel getStudentProjectInfo(@NonNull StudentProject studentProject, @NonNull StudentProjectDate studentProjectDate, SearchModel search) {
		boolean hasStudent = search.getOption("student");
		boolean hasSections = search.getOption("section");
		boolean hasTAs = search.getOption("teachingAssistants");
		boolean hasProjectInfo = search.getOption("projectInfo");
		boolean hasTestSuiteInfo = search.getOption("suiteInfo");
		
		CourseStudent courseStudent = studentProject.getStudent();
		
		StudentInfoModel model = new StudentInfoModel();
		
		model.setStudentID(courseStudent.getId());
		
		if(hasStudent) {
			Student student = courseStudent.getStudent();
			
			model.setFirstName(student.getFirstName());
			model.setLastName(student.getLastName());
		}
		
		if(hasSections)
			model.setSections(courseStudent.getSections().stream().map(Section::getSectionID).collect(Collectors.toList()));
		
		if(hasTAs)
			model.setTeachingAssistants(courseStudent.getTeachingAssistants().stream().map(CourseStudent::getId).collect(Collectors.toList()));
		
		if(hasProjectInfo) {
			model.setAdditions(Math.round(studentProjectDate.getTotalAdditions()));
			model.setDeletions(Math.round(studentProjectDate.getTotalDeletions()));
			
			model.setCommits(Math.round(studentProjectDate.getTotalCommits()));
			
			model.setMinutes(Math.round(studentProjectDate.getTotalMinutes()));
			
			model.setVisiblePoints(Math.round(studentProjectDate.getVisiblePoints()));
			model.setHiddenPoints(Math.round(studentProjectDate.getHiddenPoints()));
		}

		if(hasTestSuiteInfo) {
			Project project = studentProject.getProject();
			
			List<TestSuite> testSuites = project.getTestSuites();
			List<StudentTestSuiteInfoModel> testSuiteInfos = new ArrayList<>();
			
			for (TestSuite testSuite : testSuites) {
				StudentTestSuiteInfoModel suiteModel = new StudentTestSuiteInfoModel(testSuite.getName(), 0L, 0L);
				
				for (TestScript testScript : testSuite.getTestScripts()) {
					Long value = Math.round(testScript.getValue());
					
					suiteModel.setTotalValue(suiteModel.getTotalValue() + value);
					
					if(studentProject.getTestsPassing().contains(testScript.getId()))
						suiteModel.setCurrentValue(suiteModel.getCurrentValue() + value);
				}
				
				if(suiteModel.getTotalValue() > 0L)
					testSuiteInfos.add(suiteModel);
			}
			
			if(!testSuiteInfos.isEmpty())
				model.setTestSuites(testSuiteInfos);
		}
		
		return model;
	}
	
	@Override
	@Transactional(readOnly = true)
	public StudentInfoModel getStudentProjectInfo(@NonNull ProjectStudentSearchModel model) throws InvalidRelationIdException {
		StudentProject studentProject = getStudentProject(model.getProjectID(), model.getStudentID());

		if(studentProject.getMostRecentCommit() == null || studentProject.getMostRecentCommit().compareTo(studentProject.getFirstCommit()) == 0)
			return new StudentInfoModel();
		
		StudentProjectDate studentProjectDate = getStudentProjectDate(studentProject, model.getDate());
		
		return getStudentProjectInfo(studentProject, studentProjectDate, model);
	}
	
	@Override
	@Transactional(readOnly = true)
	public StudentProjectDiffs getStudentProjectChanges(@NonNull ProjectStudentSearchModel model) throws InvalidRelationIdException {
		StudentProject studentProject = getStudentProject(model.getProjectID(), model.getStudentID());
		Project project = studentProject.getProject();
		
		List<Commit> commits = studentProject.getCommits();
		
		StudentProjectDiffs studentProjectDiffs = new StudentProjectDiffs();
		
		if(commits.isEmpty())
			return studentProjectDiffs;
		
		studentProjectDiffs.setCommits(commits);
		
		Map<LocalDate, Integer> frequencyMap = new HashMap<>();
		
		for(Commit commit : commits) {
			LocalDate date = commit.getDate().toLocalDate();
			Integer frequency = frequencyMap.get(date);
			
			if(frequency == null)
				frequency = 1;
			else
				frequency++;
			
			frequencyMap.put(date, frequency);
		}
		
		List<FrequencyDate> frequencies = new ArrayList<>();
		
		LocalDate date = project.getStartDate();
		
		while(date.compareTo(project.getDueDate()) <= 0) {
			
			frequencies.add(new FrequencyDate(date, frequencyMap.getOrDefault(date, 0)));
			
			date = date.plusDays(1);
		}
		
		studentProjectDiffs.setFrequencies(frequencies);
		
		return studentProjectDiffs;
	}
}
