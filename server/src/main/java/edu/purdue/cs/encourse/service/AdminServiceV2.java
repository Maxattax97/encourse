package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.User;
import edu.purdue.cs.encourse.model.CourseProfessorModel;
import edu.purdue.cs.encourse.model.CourseStudentModel;
import edu.purdue.cs.encourse.model.StudentTAModel;
import lombok.NonNull;

import javax.management.relation.InvalidRelationIdException;

/**
 * Created by Killian Le Clainche on 2/1/2019.
 */
public interface AdminServiceV2 {
	
	User addUser(@NonNull User user) throws InvalidRelationIdException;
	
	void setCourseProfessor(@NonNull CourseProfessorModel model) throws InvalidRelationIdException;
	
	void addCourseTA(@NonNull CourseStudentModel model) throws InvalidRelationIdException;
	
	void removeCourseTA(@NonNull CourseStudentModel model) throws InvalidRelationIdException;
	
	void addCourseStudent(@NonNull CourseStudentModel model) throws InvalidRelationIdException;
	
	void removeCourseStudent(@NonNull CourseStudentModel model) throws InvalidRelationIdException;
	
	void addCourseStudentToTA(@NonNull StudentTAModel model) throws InvalidRelationIdException;
	
	void removeCourseStudentToTA(@NonNull StudentTAModel model) throws InvalidRelationIdException;
}
