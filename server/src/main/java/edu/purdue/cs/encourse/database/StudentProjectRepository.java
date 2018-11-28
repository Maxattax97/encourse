package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.StudentProject;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentProjectRepository extends CrudRepository<StudentProject, String> {
    List<StudentProject> findByIdStudentID(@NonNull String studentID);
    List<StudentProject> findByIdProjectID(@NonNull String projectID);
    List<StudentProject> findByIdProjectIDAndIdStudentID(@NonNull String projectID, @NonNull String studentID);
    List<StudentProject> findByIdProjectIDAndIdSuite(@NonNull String projectID, @NonNull String suite);
    List<StudentProject> findByIdStudentIDAndIdSuite(@NonNull String studentID, @NonNull String suite);
    StudentProject findByIdProjectIDAndIdStudentIDAndIdSuite(@NonNull String projectID, @NonNull String studentID, @NonNull String suite);
}