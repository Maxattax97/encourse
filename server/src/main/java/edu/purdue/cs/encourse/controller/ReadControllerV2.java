package edu.purdue.cs.encourse.controller;

import edu.purdue.cs.encourse.domain.User;
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
	
	private boolean hasAccessToAccounts(Boolean selectedAllStudents, List<Long> accounts) {
		if(accounts == null)
			return true;
		
		for(Long account : accounts) {
			//if(!adminService.hasPermissionOverAccount(getUserFromAuth(), account))
			//	return false;
		}
		return true;
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/all",
			produces = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourses() {
		return new ResponseEntity<>(courseService.getCourses(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/sections",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseSections(@Valid @NonNull @RequestBody Long courseID) {
		try {
			return new ResponseEntity<>(courseService.getCourseSections(courseID), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/students",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
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
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseTAs(@Valid @NonNull @RequestBody Long courseID) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/projects",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseProjects(@Valid @NonNull @RequestBody Long courseID) {
		try {
			return new ResponseEntity<>(courseService.getCourseProjects(courseID), HttpStatus.OK);
		}
		catch(InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	/*@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
	@RequestMapping(value = "/course/chart/tests",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseChartTests(@Valid @NonNull @RequestBody String body) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
	@RequestMapping(value = "/course/chart/suites",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseChartSuites(@Valid @NonNull @RequestBody String body) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
	@RequestMapping(value = "/course/chart/progress",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseChartProgress(@Valid @NonNull @RequestBody String body) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}*/
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/project/date",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseProjectInfoByDate(@Valid @NonNull @RequestBody CourseStudentSearch courseStudentSearch) {
		//if(courseStudentSearch.hasStudents() && !hasAccessToAccounts(courseStudentSearch.hasSelectedAllStudents(), courseStudentSearch.getStudents()))
		//	return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		
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
	
	/*@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
	@RequestMapping(value = "/course/chart/hours",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseChartHours(@Valid @NonNull @RequestBody String body) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}*/
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/course/project/timecard",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseProjectTimecardInfo(@Valid @NonNull @RequestBody CourseStudentSearch courseStudentSearch) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}
	
	/*@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
	@RequestMapping(value = "/course/project/dishonesty",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseProjectSimiliarity(@Valid @NonNull @RequestBody String body) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}*/
	
	/*@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
	@RequestMapping(value = "/course/dishonesty/velocity",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseProjectVelocity(@Valid @NonNull @RequestBody String body) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}*/
	
	/*@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
	@RequestMapping(value = "/course/project/stats",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCourseStats(@Valid @NonNull @RequestBody CourseStudentSearch courseStudentSearch) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}*/
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/student/project/info",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
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
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getStudentProjectChanges(@Valid @NonNull @RequestBody ProjectStudentSearchModel projectStudentSearch) {
		try {
			return new ResponseEntity<>(studentService.getStudentProjectChanges(projectStudentSearch), HttpStatus.OK);
		}
		catch (InvalidRelationIdException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	/*@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
	@RequestMapping(value = "/course/student/commits",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Page<?>> getStudentCommits(@Valid @NonNull @RequestBody String body) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
	@RequestMapping(value = "/course/student/chart/commits",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getStudentChartCommits(@Valid @NonNull @RequestBody String body) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
	@RequestMapping(value = "/course/student/chart/changes",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getStudentChartChanges(@Valid @NonNull @RequestBody String body) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
	@RequestMapping(value = "/course/student/chart/progress",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getStudentChartProgress(@Valid @NonNull @RequestBody String body) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'TA')")
	@RequestMapping(value = "/course/student/stats",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getStudentStats(@Valid @NonNull @RequestBody String body) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}*/
	
	@PreAuthorize("hasAnyAuthority('ADMIN', 'PROFESSOR', 'STUDENT')")
	@RequestMapping(value = "/project/tests",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.GET)
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
			method = RequestMethod.GET)
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
			method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getCurrentTask(@Valid @NonNull @RequestBody Long projectID) {
		return new ResponseEntity<>(Page.empty(), HttpStatus.OK);
	}
	
}
