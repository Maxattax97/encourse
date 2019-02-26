package edu.purdue.cs.encourse.controller;

import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.model.ProjectStudentCommitModel;
import edu.purdue.cs.encourse.model.ProjectStudentSearchModel;
import edu.purdue.cs.encourse.model.course.CourseStudentSearch;
import edu.purdue.cs.encourse.service.CourseServiceV2;
import edu.purdue.cs.encourse.service.ProfessorServiceV2;
import edu.purdue.cs.encourse.service.ProjectService;
import edu.purdue.cs.encourse.service.StudentService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.management.relation.InvalidRelationIdException;
import javax.management.relation.RelationNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * Created by Killian Le Clainche on 1/14/2019.
 */
@RestController
@RequestMapping(value="/api/v2")
@Validated
public class ReadControllerV2 {
	
	private final CourseServiceV2 courseService;
	
	private final ProfessorServiceV2 professorService;
	
	private final ProjectService projectService;
	
	private final StudentService studentService;
	
	@Autowired
	public ReadControllerV2(CourseServiceV2 courseService, ProfessorServiceV2 professorService, ProjectService projectService, StudentService studentService) {
		this.courseService = courseService;
		this.professorService = professorService;
		this.projectService = projectService;
		this.studentService = studentService;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getCourse(@Valid @NonNull @RequestBody Long courseID) {
		try {
			return new ResponseEntity<>(courseService.getCourseModel(courseID), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/all",
			produces = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getCourses() {
		return new ResponseEntity<>(courseService.getCourses(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/sections",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getCourseSections(@Valid @NonNull @RequestBody Long courseID) {
		try {
			return new ResponseEntity<>(courseService.getCourseSections(courseID), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/project/students",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getCourseStudents(@Valid @NonNull @RequestBody CourseStudentSearch courseStudentSearch) {
		try {
			return new ResponseEntity<>(courseService.getCourseProjectStudentInfo(courseStudentSearch), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		catch (IllegalAccessException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/tas",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getCourseTAs(@Valid @NonNull @RequestBody Long courseID) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/projects",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getCourseProjects(@Valid @NonNull @RequestBody Long courseID) {
		try {
			return new ResponseEntity<>(courseService.getCourseProjects(courseID), HttpStatus.OK);
		}
		catch(InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/project/date",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getCourseProjectInfoByDate(@Valid @NonNull @RequestBody CourseStudentSearch courseStudentSearch) {
		try {
			return new ResponseEntity<>(courseService.getCourseProjectInfoByDate(courseStudentSearch), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		catch (RelationNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (IllegalAccessException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/project/timecard",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getCourseProjectTimecardInfo(@Valid @NonNull @RequestBody CourseStudentSearch courseStudentSearch) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/student",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getStudent(@Valid @NonNull @RequestBody Long studentID) {
		try {
			return new ResponseEntity<>(studentService.getStudentModel(studentID), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/student/project/info",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getStudentProjectInfo(@Valid @NonNull @RequestBody ProjectStudentSearchModel projectStudentSearch) {
		try {
			return new ResponseEntity<>(studentService.getStudentProjectInfo(projectStudentSearch), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/student/project/diffs",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getStudentProjectChanges(@Valid @NonNull @RequestBody ProjectStudentSearchModel projectStudentSearch) {
		try {
			return new ResponseEntity<>(studentService.getStudentProjectChanges(projectStudentSearch), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/student/project/commit",
			produces = MediaType.TEXT_PLAIN_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getCommitDiff(@Valid @NonNull @RequestBody ProjectStudentCommitModel projectStudentCommit) {
		try {
			return new ResponseEntity<>(studentService.getStudentProjectCommitDiff(projectStudentCommit), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		catch (InterruptedException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		catch (IOException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/student/project/comparison",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getStudentComparisons(@Valid @NonNull @RequestBody ProjectStudentSearchModel projectStudentSearch) {
		try {
			return new ResponseEntity<>(studentService.getStudentProjectComparisons(projectStudentSearch), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/project/tests",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getProjectTests(@Valid @NonNull @RequestBody Long projectID) {
		try {
			return new ResponseEntity<>(projectService.getProjectTestScripts(projectID), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/project/suites",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getProjectSuites(@Valid @NonNull @RequestBody Long projectID) {
		try {
			return new ResponseEntity<>(projectService.getProjectTestSuites(projectID), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/project/task",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getCurrentTask(@Valid @NonNull @RequestBody Long projectID) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}
	
}
