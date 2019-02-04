package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.Course;
import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.Section;
import edu.purdue.cs.encourse.model.CourseModel;
import edu.purdue.cs.encourse.model.ProjectModel;
import edu.purdue.cs.encourse.model.SectionModel;
import edu.purdue.cs.encourse.model.StudentInfoModel;
import edu.purdue.cs.encourse.model.course.CourseStudentSearch;
import edu.purdue.cs.encourse.model.ProjectInfoModel;
import lombok.NonNull;

import javax.management.relation.InvalidRelationIdException;
import javax.management.relation.RelationException;
import javax.management.relation.RelationNotFoundException;
import java.util.List;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
public interface CourseServiceV2 {
	
	Course getCourse(@NonNull Long courseID) throws InvalidRelationIdException;
	
	Section getSection(@NonNull Long sectionID) throws InvalidRelationIdException;
	
	Course addCourse(@NonNull CourseModel course) throws RelationException, IllegalArgumentException;
	
	Section addSection(@NonNull SectionModel section) throws InvalidRelationIdException, IllegalArgumentException;
	
	List<Course> getCourses();
	
	List<Section> getCourseSections(@NonNull Long courseID) throws InvalidRelationIdException;
	
	List<ProjectModel> getCourseProjects(Long courseID) throws InvalidRelationIdException;
	
	List<StudentInfoModel> getCourseProjectStudentInfo(@NonNull CourseStudentSearch courseStudentSearch) throws InvalidRelationIdException, NullPointerException;
	
	Project validateCourseStudentSearch(@NonNull CourseStudentSearch courseStudentSearch) throws InvalidRelationIdException, RelationNotFoundException;
	ProjectInfoModel getCourseProjectInfoByDate(@NonNull CourseStudentSearch courseStudentSearch) throws InvalidRelationIdException, RelationNotFoundException;
}
