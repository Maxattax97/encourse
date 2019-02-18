package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.CourseRepository;
import edu.purdue.cs.encourse.database.ProjectDateRepository;
import edu.purdue.cs.encourse.database.ProjectRepository;
import edu.purdue.cs.encourse.database.SectionRepository;
import edu.purdue.cs.encourse.database.StudentComparisonRepository;
import edu.purdue.cs.encourse.database.StudentProjectDateRepository;
import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.Course;
import edu.purdue.cs.encourse.domain.Professor;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.ProjectDate;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import edu.purdue.cs.encourse.domain.relations.StudentComparison;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.BasicStatistics;
import edu.purdue.cs.encourse.model.CourseModel;
import edu.purdue.cs.encourse.model.CourseSectionModel;
import edu.purdue.cs.encourse.model.DoubleRange;
import edu.purdue.cs.encourse.model.ProjectModel;
import edu.purdue.cs.encourse.model.SectionModel;
import edu.purdue.cs.encourse.model.CourseBarChartModel;
import edu.purdue.cs.encourse.model.StudentInfoModel;
import edu.purdue.cs.encourse.model.ProjectInfoModel;
import edu.purdue.cs.encourse.model.course.CourseStudentFilters;
import edu.purdue.cs.encourse.model.course.CourseStudentSearch;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.AdminServiceV2;
import edu.purdue.cs.encourse.service.CourseServiceV2;
import edu.purdue.cs.encourse.service.StudentService;
import edu.purdue.cs.encourse.service.helper.ChartHelper;
import lombok.NonNull;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.InvalidRelationIdException;
import javax.management.relation.RelationException;
import javax.management.relation.RelationNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
	private StudentComparisonRepository studentComparisonRepository;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AdminServiceV2 adminService;
	
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
	public CourseModel getCourseModel(@NonNull Long courseID) throws InvalidRelationIdException {
		Course course = getCourse(courseID);
		
		return new CourseModel(course.getProfessor().getUserID(), course.getCRN(), course.getTitle(), course.getName(), course.getSemester(), course.getRemotePath());
	}
	
	@Override
	public Section getSection(@NonNull Long sectionID) throws InvalidRelationIdException {
		Optional<Section> sectionOptional = sectionRepository.findById(sectionID);
		
		if(!sectionOptional.isPresent())
			throw new InvalidRelationIdException("Section ID (" + sectionID + ") does not exist in the database.");
		
		return sectionOptional.get();
	}
	
	private CourseBarChartModel getCourseHistogram(int count, boolean exists, double[] samples, BasicStatistics stats) {
		CourseBarChartModel histogram = new CourseBarChartModel();
		
		if(exists) {
			histogram.setStats(new DescriptiveStatistics(samples));
			histogram.setBars(ChartHelper.toBarChart(count, stats, samples));
		}
		
		if(samples != null) {
			histogram.setCourseStats(stats);
			return histogram;
		}
		
		return null;
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
	public Section addSection(@NonNull CourseSectionModel model) throws InvalidRelationIdException, IllegalArgumentException {
		Course course = getCourse(model.getCourseID());
		
		Section section = sectionRepository.save(new Section(course, model));
		
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
	public List<SectionModel> getCourseSections(@NonNull Long courseID) throws InvalidRelationIdException {
		return getCourse(courseID).getSections().stream().map(section -> new SectionModel(section.getSectionID(), section.getType(), section.getTime())).collect(Collectors.toList());
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProjectModel> getCourseProjects(@NonNull Long courseID) throws InvalidRelationIdException {
		Course course = getCourse(courseID);
		
		List<ProjectModel> projects = new ArrayList<>();
		
		for(Project project : course.getProjects()) {
			ProjectModel model = new ProjectModel(project.getName(), project.getStartDate(), project.getDueDate(), project.getRepository(), project.getRunTestall());
			
			model.setProjectID(project.getProjectID());
			
			if(project.getRunTestall()) {
				model.setTotalVisiblePoints(project.getTotalVisiblePoints());
				model.setTotalHiddenPoints(project.getTotalHiddenPoints());
			}
			
			projects.add(model);
		}
		
		return projects;
	}
	
	@Override
	public Project validateCourseStudentSearch(@NonNull CourseStudentSearch courseStudentSearch, boolean allowAnonymous) throws InvalidRelationIdException, NullPointerException, IllegalAccessException {
		if(courseStudentSearch.getProjectID() == null)
			throw new NullPointerException("Project ID was not provided.");
		
		Optional<Project> projectOptional = projectRepository.findById(courseStudentSearch.getProjectID());
		
		if(!projectOptional.isPresent())
			throw new InvalidRelationIdException("Project ID (" + courseStudentSearch.getProjectID() + ") does not exist in the database.");
		
		Project project = projectOptional.get();
		
		if(!courseStudentSearch.hasDate())
			courseStudentSearch.setDate(project.getAnalyzeDateTime().compareTo(project.getDueDate()) < 0 ? project.getAnalyzeDateTime().compareTo(project.getStartDate()) > 0 ? project.getAnalyzeDateTime() : project.getStartDate() : project.getDueDate());
		else
			courseStudentSearch.setDate(courseStudentSearch.getDate().compareTo(project.getDueDate()) < 0 ? courseStudentSearch.getDate().compareTo(project.getStartDate()) > 0 ? courseStudentSearch.getDate() : project.getStartDate() : project.getDueDate());
		
		Account account = accountService.getAccount(adminService.getUser().getId());
		Course course = project.getCourse();
		
		final CourseStudentFilters filters = courseStudentSearch.hasFilters() ? courseStudentSearch.getFilters() : new CourseStudentFilters();
		courseStudentSearch.setFilters(filters);
		
		if(account.getRole() == Account.Role.PROFESSOR) {
			Professor professor = accountService.getProfessor(account.getUserID());
			
			if(!course.getProfessor().getUserID().equals(professor.getUserID()))
				throw new IllegalAccessException("You are not the course professor.");
		}
		else if(account.getRole() == Account.Role.STUDENT) {
			Student student = accountService.getStudent(account.getUserID());
			
			CourseStudent courseStudent = null;
			
			for(CourseStudent studentIter : student.getCourses()) {
				if(studentIter.getCourse().getCourseID().equals(course.getCourseID())) {
					courseStudent = studentIter;
					break;
				}
			}
			
			if(courseStudent == null || courseStudent.getIsStudent())
				throw new IllegalAccessException("You are not a teaching assistant for this course.");
			else {
				final List<CourseStudent> students = courseStudent.getStudents();
				
				if(filters.getStudents() == null) {
					if(allowAnonymous && (filters.getSelectedAll() == null || !filters.getSelectedAll())) {
						filters.setStudents(Collections.singletonList(-1L));
						filters.setSelectedAll(true);
					}
					else {
						filters.setStudents(students.stream().map(CourseStudent::getId).collect(Collectors.toList()));
						filters.setSelectedAll(false);
					}
				}
				else {
					if(filters.getSelectedAll() != null && filters.getSelectedAll())
						filters.setStudents(students.stream().filter(s -> !filters.getStudents().contains(s.getId())).map(CourseStudent::getId).collect(Collectors.toList()));
					else
						filters.setStudents(students.stream().filter(s -> filters.getStudents().contains(s.getId())).map(CourseStudent::getId).collect(Collectors.toList()));
						
					filters.setSelectedAll(false);
				}
			}
		}
		
		if(filters.getCommits() == null)
			filters.setCommits(new DoubleRange(-1.0, 10000000.0));
		
		filters.getCommits().populate();
		
		if(filters.getTime() == null)
			filters.setTime(new DoubleRange(-1.0, 10000000.0));
		
		filters.getTime().populate();
		
		if(filters.getProgress() == null)
			filters.setProgress(new DoubleRange(-1.0, 10000000.0));
		
		filters.getProgress().populate();
		
		if(filters.getStudents() == null || filters.getStudents().size() == 0) {
			filters.setSelectedAll(true);
			filters.setStudents(Collections.singletonList(-1L));
		}
		
		if(filters.getSelectedAll() == null)
			filters.setSelectedAll(false);
		
		return project;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<StudentInfoModel> getCourseProjectStudentInfo(@NonNull CourseStudentSearch courseStudentSearch) throws InvalidRelationIdException, NullPointerException, IllegalAccessException {
		Project project = validateCourseStudentSearch(courseStudentSearch, false);
		
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
	public ProjectInfoModel getCourseProjectInfoByDate(@NonNull CourseStudentSearch courseStudentSearch) throws InvalidRelationIdException, RelationNotFoundException, IllegalAccessException {
		ProjectInfoModel projectInfo = new ProjectInfoModel();
		
		boolean hasProgress = courseStudentSearch.getOption("progress");
		boolean hasCommit = courseStudentSearch.getOption("commit");
		boolean hasTime = courseStudentSearch.getOption("time");
		boolean hasAddition = courseStudentSearch.getOption("addition");
		boolean hasDeletion = courseStudentSearch.getOption("deletion");
		
		boolean hasChanges = courseStudentSearch.getOption("changes");
		boolean hasSimilarity = courseStudentSearch.getOption("similarity");
		boolean hasTimeVelocity = courseStudentSearch.getOption("timeVelocity");
		boolean hasCommitVelocity = courseStudentSearch.getOption("commitVelocity");
		
		if(!hasProgress && !hasCommit && !hasTime && !hasAddition && !hasDeletion && !hasChanges && !hasSimilarity && !hasTimeVelocity && !hasCommitVelocity)
			return projectInfo;
		
		Project project = validateCourseStudentSearch(courseStudentSearch, true);
		
		ProjectDate projectDate = projectDateRepository.findFirstByProjectAndDate(project, courseStudentSearch.getDate());
		
		if(projectDate == null)
			throw new RelationNotFoundException("Query in ProjectDateRepository with parameters(project=" + courseStudentSearch.getProjectID() + ", date=" + courseStudentSearch.getDate() + ") yielded no results.");
		
		List<StudentProjectDate> students = studentProjectDateRepository.findAllByCourseStudentSearch(project, courseStudentSearch);
		
		if(students.isEmpty())
			return projectInfo;
		
		boolean includeVisibleTests = !courseStudentSearch.getView().equalsIgnoreCase("Hidden");
		boolean includeHiddenTests = !courseStudentSearch.getView().equalsIgnoreCase("Visible");
		
		double[] progressSamples = hasProgress ? new double[students.size()] : null;
		double[] commitSamples = hasCommit ? new double[students.size()] : null;
		double[] timeSamples = hasTime ? new double[students.size()] : null;
		double[] additionSamples = hasAddition ? new double[students.size()] : null;
		double[] deletionSamples = hasDeletion ? new double[students.size()] : null;
		
		double[] changesSamples = hasChanges ? new double[students.size()] : null;
		double[] timeVelocitySamples = hasTimeVelocity ? new double[students.size()] : null;
		double[] commitVelocitySamples = hasCommitVelocity ? new double[students.size()] : null;
		
		for(int i = 0; i < students.size(); i++) {
			StudentProjectDate studentProjectDate = students.get(i);
			StudentProject studentProject = studentProjectDate.getStudentProject();
			
			Double commits = studentProjectDate.getTotalCommits() == null ? 0.0 : studentProjectDate.getTotalCommits();
			Double minutes = studentProjectDate.getTotalMinutes() == null ? 0.0 : studentProjectDate.getTotalMinutes();
			Double additions = studentProjectDate.getTotalAdditions() == null ? 0.0 : studentProjectDate.getTotalAdditions();
			Double deletions = studentProjectDate.getTotalDeletions() == null ? 0.0 : studentProjectDate.getTotalDeletions();
			
			double visiblePoints = studentProjectDate.getVisiblePoints() == null ? 0.0 : studentProjectDate.getVisiblePoints();
			double hiddenPoints = studentProjectDate.getHiddenPoints() == null ? 0.0 : studentProjectDate.getHiddenPoints();
			
			if(progressSamples != null) {
				if(project.getRunTestall())
					progressSamples[i] = (includeVisibleTests ? visiblePoints : 0) + (includeHiddenTests ? hiddenPoints : 0);
				else
					progressSamples[i] = 0;
			}
			
			if(commitSamples != null)
				commitSamples[i] = commits;
			
			if(timeSamples != null)
				timeSamples[i] = minutes;
			
			if(additionSamples != null)
				additionSamples[i] = additions;
			
			if(deletionSamples != null)
				deletionSamples[i] = deletions;
			
			if(changesSamples != null)
				changesSamples[i] = studentProject.getChanges();
			
			if(timeVelocitySamples != null)
				timeVelocitySamples[i] = studentProject.getTimeVelocity();
			
			if(commitVelocitySamples != null)
				commitVelocitySamples[i] = studentProject.getCommitVelocity();
		}
		
		int studentCount = project.getCourse().getStudentCount();
		
		if(hasSimilarity) {
			List<Long> studentIds = students.stream().map(student -> student.getStudentProject().getId()).collect(Collectors.toList());
			List<StudentComparison> comparisons = studentComparisonRepository.findAllByStudentProject1_IdInOrStudentProject2_IdIn(studentIds, studentIds);
			double[] similaritySamples = new double[comparisons.size()];
			
			int i = 0;
			
			for(StudentComparison comparison : comparisons) {
				similaritySamples[i] = comparison.getCount();
				i++;
			}
			
			projectInfo.setSimilarity(getCourseHistogram((studentCount * (studentCount - 1)) / 2, hasSimilarity, similaritySamples, project.getSimilarityStats()));
		}
		
		projectInfo.setProgress(getCourseHistogram(studentCount, hasProgress, progressSamples, !includeHiddenTests ? projectDate.getHiddenPointStats() : !includeVisibleTests ? projectDate.getVisiblePointStats() : projectDate.getTotalPointStats()));
		projectInfo.setCommits(getCourseHistogram(studentCount, hasCommit, commitSamples, projectDate.getCommitStats()));
		projectInfo.setTime(getCourseHistogram(studentCount, hasTime, timeSamples, projectDate.getMinuteStats()));
		
		projectInfo.setChanges(getCourseHistogram(studentCount, hasChanges, changesSamples, project.getChangesStats()));
		projectInfo.setTimeVelocity(getCourseHistogram(studentCount, hasTimeVelocity, timeVelocitySamples, project.getTimeVelocityStats()));
		projectInfo.setCommitVelocity(getCourseHistogram(studentCount, hasCommitVelocity, commitVelocitySamples, project.getCommitVelocityStats()));
		
		return projectInfo;
	}
}
