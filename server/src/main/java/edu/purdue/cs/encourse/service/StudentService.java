package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.StudentInfoModel;
import edu.purdue.cs.encourse.model.ProjectStudentSearchModel;
import edu.purdue.cs.encourse.model.SearchModel;
import edu.purdue.cs.encourse.model.student.StudentProjectDiffs;
import lombok.NonNull;

import javax.management.relation.InvalidRelationIdException;
import java.time.LocalDate;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
public interface StudentService {
	
	CourseStudent getStudent(@NonNull Long studentID) throws InvalidRelationIdException;
	
	StudentProject getStudentProject(@NonNull Long projectID, @NonNull Long studentID) throws InvalidRelationIdException;
	
	StudentProjectDate getStudentProjectDate(@NonNull StudentProject studentProject, LocalDate date) throws InvalidRelationIdException;
	
	StudentProjectDate getStudentProjectDate(@NonNull Long projectID, @NonNull Long studentID, LocalDate date) throws InvalidRelationIdException;
	
	StudentInfoModel getStudentProjectInfo(@NonNull StudentProject studentProject, @NonNull StudentProjectDate studentProjectDate, SearchModel search);
	
	StudentInfoModel getStudentProjectInfo(@NonNull ProjectStudentSearchModel model) throws InvalidRelationIdException;
	
	StudentProjectDiffs getStudentProjectChanges(@NonNull ProjectStudentSearchModel model) throws InvalidRelationIdException;
}
