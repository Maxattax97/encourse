package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.relations.StudentProject;
import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import edu.purdue.cs.encourse.model.course.CourseStudentFilters;
import edu.purdue.cs.encourse.model.course.CourseStudentSearch;
import lombok.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentProjectRepository extends PagingAndSortingRepository<StudentProject, Long> {
    Optional<StudentProject> findByProject_ProjectIDAndStudent_Id(@NonNull Long projectID, @NonNull Long studentID);
}