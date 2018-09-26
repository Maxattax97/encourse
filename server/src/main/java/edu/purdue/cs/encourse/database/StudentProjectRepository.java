package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.StudentProject;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentProjectRepository extends CrudRepository<StudentProject, Long> {
    List<StudentProject> findByIdStudentID(@NonNull String studentID);
    List<StudentProject> findByIdProjectIdentifier(@NonNull String projectID);
    List<StudentProject> findByCurrentGrade(String grade);
}