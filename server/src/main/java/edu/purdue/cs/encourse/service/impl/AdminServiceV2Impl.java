package edu.purdue.cs.encourse.service.impl;

import edu.purdue.cs.encourse.database.AccountRepository;
import edu.purdue.cs.encourse.database.AuthorityRepository;
import edu.purdue.cs.encourse.database.CourseRepository;
import edu.purdue.cs.encourse.database.CourseStudentRepository;
import edu.purdue.cs.encourse.database.UserRepository;
import edu.purdue.cs.encourse.domain.Account;
import edu.purdue.cs.encourse.domain.Course;
import edu.purdue.cs.encourse.domain.Professor;
import edu.purdue.cs.encourse.domain.Student;
import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import edu.purdue.cs.encourse.model.CourseProfessorModel;
import edu.purdue.cs.encourse.model.CourseStudentModel;
import edu.purdue.cs.encourse.model.StudentTAModel;
import edu.purdue.cs.encourse.model.UserModel;
import edu.purdue.cs.encourse.service.AccountService;
import edu.purdue.cs.encourse.service.AdminServiceV2;
import edu.purdue.cs.encourse.service.CourseServiceV2;
import edu.purdue.cs.encourse.service.StudentService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.management.relation.InvalidRelationIdException;
import java.util.List;

/**
 * Created by Killian Le Clainche on 2/1/2019.
 */
@Service(value = AdminServiceV2Impl.NAME)
public class AdminServiceV2Impl implements AdminServiceV2 {
	
	final static String NAME = "AdminServiceV2";
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CourseStudentRepository courseStudentRepository;
	
	@Autowired
	private CourseServiceV2 courseService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Override
	public User getUser() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return ((User)securityContext.getAuthentication().getPrincipal());
	}
	
	@Override
	public User addUser(@NonNull UserModel model) throws InvalidRelationIdException {
		Account account = accountService.getAccount(model.getUserID());
		
		User user = new User(model);
		
		user.getAuthorities().add(authorityRepository.findDistinctByName(account.getRole().name()));
		
		userRepository.save(user);
		
		return user;
	}
	
	@Override
	public void setCourseProfessor(@NonNull CourseProfessorModel model) throws InvalidRelationIdException {
		Course course = courseService.getCourse(model.getCourseID());
		Professor currentProfessor = course.getProfessor();
		Professor professor = accountService.getProfessor(model.getProfessorID());
		
		currentProfessor.getCourses().remove(course);
		professor.getCourses().add(course);
		
		course.setProfessor(professor);
		
		accountRepository.save(professor);
		accountRepository.save(currentProfessor);
	}
	
	@Override
	public void addCourseTA(@NonNull CourseStudentModel model) throws InvalidRelationIdException {
		Course course = courseService.getCourse(model.getCourseID());
		Student student = accountService.getStudent(model.getStudentID());
		
		CourseStudent courseStudent = new CourseStudent(course, student, false);
		
		course.getTeachingAssistants().add(courseStudent);
		
		courseRepository.save(course);
	}
	
	@Override
	public void removeCourseTA(@NonNull CourseStudentModel model) throws InvalidRelationIdException {
		Course course = courseService.getCourse(model.getCourseID());
		
		List<CourseStudent> tas = course.getTeachingAssistants();
		
		CourseStudent ta;
		
		for(int i = 0; i < tas.size(); i++) {
			ta = tas.get(i);
			
			if(ta.getStudent().getUserID().equals(model.getStudentID())) {
				tas.remove(i);
				
				List<CourseStudent> students = ta.getStudents();
				
				for(CourseStudent student : students)
					student.getTeachingAssistants().remove(ta);
				
				break;
			}
		}
		
		courseRepository.save(course);
	}
	
	@Override
	public void addCourseStudent(@NonNull CourseStudentModel model) throws InvalidRelationIdException {
		Course course = courseService.getCourse(model.getCourseID());
		Student student = accountService.getStudent(model.getStudentID());
		
		CourseStudent courseStudent = new CourseStudent(course, student, true);
		
		course.getStudents().add(courseStudent);
		course.setStudentCount(course.getStudentCount() + 1);
		
		courseRepository.save(course);
	}
	
	@Override
	public void removeCourseStudent(@NonNull CourseStudentModel model) throws InvalidRelationIdException {
		Course course = courseService.getCourse(model.getCourseID());
		
		List<CourseStudent> students = course.getStudents();
		
		for(int i = 0; i < students.size(); i++) {
			CourseStudent student = students.get(i);
			
			if(student.getStudent().getUserID().equals(model.getStudentID())) {
				students.remove(i);
				
				List<CourseStudent> tas = student.getStudents();
				
				for(CourseStudent ta : tas)
					ta.getStudents().remove(student);
				
				break;
			}
		}
		
		courseRepository.save(course);
	}
	
	@Override
	public void addCourseStudentToTA(@NonNull StudentTAModel model) throws InvalidRelationIdException {
		CourseStudent student = studentService.getStudent(model.getStudentID());
		CourseStudent ta = studentService.getStudent(model.getTeachingAssistantID());
		
		student.getTeachingAssistants().add(ta);
		
		ta.getStudents().add(student);
		
		courseStudentRepository.save(student);
		courseStudentRepository.save(ta);
	}
	
	@Override
	public void removeCourseStudentToTA(@NonNull StudentTAModel model) throws InvalidRelationIdException {
		CourseStudent student = studentService.getStudent(model.getStudentID());
		CourseStudent ta = studentService.getStudent(model.getTeachingAssistantID());
		
		student.getTeachingAssistants().remove(ta);
		
		ta.getStudents().remove(student);
		
		courseStudentRepository.save(student);
		courseStudentRepository.save(ta);
	}
	
}
