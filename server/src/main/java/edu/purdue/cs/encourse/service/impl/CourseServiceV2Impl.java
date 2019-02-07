package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.CourseRepository;
import edu.purdue.cs.encourse.database.ProjectDateRepository;
import edu.purdue.cs.encourse.database.ProjectRepository;
import edu.purdue.cs.encourse.database.SectionRepository;
import edu.purdue.cs.encourse.database.StudentProjectDateRepository;
import edu.purdue.cs.encourse.domain.Course;
import edu.purdue.cs.encourse.domain.Professor;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.ProjectDate;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.BasicStatistics;
import edu.purdue.cs.encourse.model.CourseModel;
import edu.purdue.cs.encourse.model.ProjectModel;
import edu.purdue.cs.encourse.model.SectionModel;
import edu.purdue.cs.encourse.model.CourseBarChartModel;
import edu.purdue.cs.encourse.model.StudentInfoModel;
import edu.purdue.cs.encourse.model.ProjectInfoModel;
import edu.purdue.cs.encourse.model.course.CourseStudentSearch;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.CourseServiceV2;
import edu.purdue.cs.encourse.service.StudentService;
import edu.purdue.cs.encourse.service.helper.ChartHelper;
import lombok.NonNull;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.InvalidRelationIdException;
import javax.management.relation.RelationException;
import javax.management.relation.RelationNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
@Service(value = CourseServiceV2Impl.NAME)
public class CourseServiceV2Impl implements CourseServiceV2 {
	
	final static String NAME = "CourseServiceV2";
	
	private final CourseRepository courseRepository;
	
	private final StudentProjectDateRepository studentProjectDateRepository;
	
	private final ProjectRepository projectRepository;
	
	private final ProjectDateRepository projectDateRepository;
	
	private final SectionRepository sectionRepository;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	public CourseServiceV2Impl(StudentProjectDateRepository StudentProjectDateRepository, ProjectRepository projectRepository, ProjectDateRepository projectDateRepository, CourseRepository courseRepository, SectionRepository sectionRepository) {
		this.studentProjectDateRepository = StudentProjectDateRepository;
		this.projectRepository = projectRepository;
		this.projectDateRepository = projectDateRepository;
		this.courseRepository = courseRepository;
		this.sectionRepository = sectionRepository;
	}
	
	@Override
	public Course getCourse(@NonNull Long courseID) throws InvalidRelationIdException {
		Optional<Course> courseOptional = courseRepository.findById(courseID);
		
		if(!courseOptional.isPresent())
			throw new InvalidRelationIdException("Course ID (" + courseID + ") does not exist in the database.");
		
		return courseOptional.get();
	}
	
	@Override
	public Section getSection(@NonNull Long sectionID) throws InvalidRelationIdException {
		Optional<Section> sectionOptional = sectionRepository.findById(sectionID);
		
		if(!sectionOptional.isPresent())
			throw new InvalidRelationIdException("Section ID (" + sectionID + ") does not exist in the database.");
		
		return sectionOptional.get();
	}
	
	private CourseBarChartModel getCourseHistogram(int count, boolean hasStats, boolean hasChart, double[] samples, BasicStatistics stats) {
		CourseBarChartModel histogram = new CourseBarChartModel();
		
		if(hasStats)
			histogram.setStats(new DescriptiveStatistics(samples));
		
		if(hasChart)
			histogram.setBars(ChartHelper.toBarChart(count, stats, samples));
		
		if(samples != null) {
			histogram.setCourseStats(stats);
			return histogram;
		}
		
		return null;
	}
	
	private User getUserFromAuth() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return ((User)securityContext.getAuthentication().getPrincipal());
	}
	
	@Override
	@Transactional
	public Course addCourse(@NonNull CourseModel model) throws RelationException, IllegalArgumentException {
		Professor professor = accountService.getProfessor(model.getProfessorID());
		
		Course course = courseRepository.save(new Course(professor, model));
		
		if(course == null)
			throw new RelationException("Could not create new account object in database.");
		
		return course;
	}
	
	@Override
	@Transactional
	public Section addSection(@NonNull SectionModel model) throws InvalidRelationIdException, IllegalArgumentException {
		Course course = getCourse(model.getCourseID());
		
		Section section = new Section(course, model);
		
		course.getSections().add(section);
		
		courseRepository.save(course);
		
		return section;
	}
	
	@Override
	public List<Course> getCourses() {
		return null;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Section> getCourseSections(@NonNull Long courseID) throws InvalidRelationIdException {
		return getCourse(courseID).getSections();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProjectModel> getCourseProjects(@NonNull Long courseID) throws InvalidRelationIdException {
		Course course = getCourse(courseID);
		
		List<ProjectModel> projects = new ArrayList<>();
		
		for(Project project : course.getProjects()) {
			ProjectModel model = new ProjectModel(project.getName(), project.getStartDate(), project.getDueDate(), project.getRepository());
			
			model.setProjectID(project.getProjectID());
			
			model.setTotalVisiblePoints(project.getTotalVisiblePoints());
			model.setTotalHiddenPoints(project.getTotalHiddenPoints());
		}
		
		return projects;
	}
	
	@Override
	public Project validateCourseStudentSearch(@NonNull CourseStudentSearch courseStudentSearch) throws InvalidRelationIdException, NullPointerException {
		if(courseStudentSearch.getProjectID() == null)
			throw new NullPointerException("Project ID was not provided.");
		
		Optional<Project> projectOptional = projectRepository.findById(courseStudentSearch.getProjectID());
		
		if(!projectOptional.isPresent())
			throw new InvalidRelationIdException("Project ID (" + courseStudentSearch.getProjectID() + ") does not exist in the database.");
		
		Project project = projectOptional.get();
		
		if(!courseStudentSearch.hasDate())
			courseStudentSearch.setDate(project.getAnalyzeDateTime().compareTo(project.getDueDate()) < 0 ? project.getAnalyzeDateTime() : project.getDueDate());
		
		return project;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<StudentInfoModel> getCourseProjectStudentInfo(@NonNull CourseStudentSearch courseStudentSearch) throws InvalidRelationIdException, NullPointerException {
		Project project = validateCourseStudentSearch(courseStudentSearch);
		
		List<StudentProjectDate> studentProjectDates = studentProjectDateRepository.findAllByCourseStudentSearch(project, courseStudentSearch);
		
		List<StudentInfoModel> students = new ArrayList<>(studentProjectDates.size());
		
		if(courseStudentSearch.hasOptions())
			courseStudentSearch.getOptions().put("suiteInfo", false);
		
		for(StudentProjectDate studentProjectDate : studentProjectDates)
			students.add(studentService.getStudentProjectInfo(studentProjectDate.getStudentProject(), studentProjectDate, courseStudentSearch));
		
		return students;
	}
	
	@Override
	@Transactional(readOnly = true)
	public ProjectInfoModel getCourseProjectInfoByDate(@NonNull CourseStudentSearch courseStudentSearch) throws InvalidRelationIdException, RelationNotFoundException {
		ProjectInfoModel projectInfo = new ProjectInfoModel();
		
		boolean hasProgressStats = courseStudentSearch.getOption("progressStats");
		boolean hasProgressChart = courseStudentSearch.getOption("progressChart");
		boolean hasCommitStats = courseStudentSearch.getOption("commitStats");
		boolean hasCommitChart = courseStudentSearch.getOption("commitChart");
		boolean hasTimeStats = courseStudentSearch.getOption("timeStats");
		boolean hasTimeChart = courseStudentSearch.getOption("timeChart");
		
		boolean hasChangesStats = courseStudentSearch.getOption("changesStats");
		boolean hasChangesChart = courseStudentSearch.getOption("changesChart");
		boolean hasSimilarityStats = courseStudentSearch.getOption("similarityStats");
		boolean hasSimilarityChart = courseStudentSearch.getOption("similarityChart");
		boolean hasTimeVelocityStats = courseStudentSearch.getOption("timeVelocityStats");
		boolean hasTimeVelocityChart = courseStudentSearch.getOption("timeVelocityChart");
		boolean hasCommitVelocityStats = courseStudentSearch.getOption("commitVelocityStats");
		boolean hasCommitVelocityChart = courseStudentSearch.getOption("commitVelocityChart");
		
		if(!hasProgressStats && !hasProgressChart && !hasCommitStats && !hasCommitChart && !hasTimeStats && !hasTimeChart &&
			!hasChangesStats && !hasChangesChart && !hasSimilarityStats && !hasSimilarityChart && !hasTimeVelocityStats && !hasTimeVelocityChart && !hasCommitVelocityStats && !hasCommitVelocityChart)
			return projectInfo;
		
		Project project = validateCourseStudentSearch(courseStudentSearch);
		
		ProjectDate projectDate = projectDateRepository.findFirstByProjectAndDate(project, courseStudentSearch.getDate());
		
		if(projectDate == null)
			throw new RelationNotFoundException("Query in ProjectDateRepository with parameters(project=" + courseStudentSearch.getProjectID() + ", date=" + courseStudentSearch.getDate() + ") yielded no results.");
		
		List<StudentProjectDate> students = studentProjectDateRepository.findAllByCourseStudentSearch(project, courseStudentSearch);
		
		if(students.isEmpty())
			return projectInfo;
		
		boolean includeVisibleTests = !courseStudentSearch.getView().equalsIgnoreCase("Hidden");
		boolean includeHiddenTests = !courseStudentSearch.getView().equalsIgnoreCase("Visible");
		
		double[] progressSamples = hasProgressStats || hasProgressChart ? new double[students.size()] : null;
		double[] commitSamples = hasCommitStats || hasCommitChart ? new double[students.size()] : null;
		double[] timeSamples = hasTimeStats || hasTimeChart ? new double[students.size()] : null;
		
		double[] changesSamples = hasChangesStats || hasChangesChart ? new double[students.size()] : null;
		double[] similaritySamples = hasSimilarityStats || hasSimilarityChart ? new double[students.size()] : null;
		double[] timeVelocitySamples = hasTimeVelocityStats || hasTimeVelocityChart ? new double[students.size()] : null;
		double[] commitVelocitySamples = hasCommitVelocityStats || hasCommitVelocityChart ? new double[students.size()] : null;
		
		for(int i = 0; i < students.size(); i++) {
			StudentProjectDate student = students.get(i);
			
			if(progressSamples != null)
				progressSamples[i] = (includeVisibleTests ? student.getVisiblePoints() : 0) + (includeHiddenTests ? student.getHiddenPoints() : 0);
			
			if(commitSamples != null)
				commitSamples[i] = student.getTotalCommits();
			
			if(timeSamples != null)
				timeSamples[i] = student.getTotalMinutes();
			
			if(changesSamples != null)
				changesSamples[i] = (student.getTotalAdditions() / student.getTotalDeletions());
			
			//TODO Similarity
			//if(buildSimilarityStats != null)
			//	buildSimilarityStats[i] = stud;
			
			if(timeVelocitySamples != null)
				timeVelocitySamples[i] = (student.getVisiblePoints() + student.getHiddenPoints()) / student.getTotalMinutes();
			
			if(commitVelocitySamples != null)
				commitVelocitySamples[i] = (student.getVisiblePoints() + student.getHiddenPoints()) / student.getTotalCommits();
		}
		
		int studentCount = project.getCourse().getStudentCount();
		
		projectInfo.setProgress(getCourseHistogram(studentCount, hasProgressStats, hasProgressChart, progressSamples, !includeHiddenTests ? projectDate.getHiddenPointStats() : !includeVisibleTests ? projectDate.getVisiblePointStats() : projectDate.getTotalPointStats()));
		projectInfo.setCommits(getCourseHistogram(studentCount, hasCommitStats, hasCommitChart, commitSamples, projectDate.getCommitStats()));
		projectInfo.setTime(getCourseHistogram(studentCount, hasTimeStats, hasTimeChart, timeSamples, projectDate.getMinuteStats()));
		
		projectInfo.setChanges(getCourseHistogram(studentCount, hasChangesStats, hasChangesChart, changesSamples, projectDate.getChangesStats()));
		projectInfo.setTimeVelocity(getCourseHistogram(studentCount, hasTimeVelocityStats, hasTimeVelocityChart, timeVelocitySamples, projectDate.getTimeVelocityStats()));
		projectInfo.setCommitVelocity(getCourseHistogram(studentCount, hasCommitVelocityStats, hasCommitVelocityChart, commitVelocitySamples, projectDate.getCommitVelocityStats()));
		
		return projectInfo;
	}
}
