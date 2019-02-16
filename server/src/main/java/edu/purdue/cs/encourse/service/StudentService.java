package edu.purdue.cs.encourse.service;

import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.ProjectStudentCommitModel;
import edu.purdue.cs.encourse.model.StudentInfoModel;
import edu.purdue.cs.encourse.model.ProjectStudentSearchModel;
import edu.purdue.cs.encourse.model.SearchModel;
import edu.purdue.cs.encourse.model.student.StudentProjectDiffs;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.InvalidRelationIdException;
import java.time.LocalDate;

/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
public interface StudentService {
	
	@Transactional(readOnly = true)
	CourseStudent getStudent(@NonNull Long studentID) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
	StudentInfoModel getStudentModel(@NonNull Long studentID) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
	StudentProject getStudentProject(@NonNull Long projectID, @NonNull Long studentID) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
	StudentProjectDate getStudentProjectDate(@NonNull StudentProject studentProject, LocalDate date) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
	StudentProjectDate getStudentProjectDate(@NonNull Long projectID, @NonNull Long studentID, LocalDate date) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
	StudentInfoModel getStudentProjectInfo(@NonNull StudentProject studentProject, @NonNull StudentProjectDate studentProjectDate, SearchModel search);
	
	@Transactional(readOnly = true)
	StudentInfoModel getStudentProjectInfo(@NonNull ProjectStudentSearchModel model) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
	StudentProjectDiffs getStudentProjectChanges(@NonNull ProjectStudentSearchModel model) throws InvalidRelationIdException;
	
	@Transactional(readOnly = true)
	Object getStudentProjectCommitDiff(@NonNull ProjectStudentCommitModel model) throws InvalidRelationIdException;
}
