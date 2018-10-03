package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.StudentProject;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentProjectRepository extends CrudRepository<StudentProject, String> {
    List<StudentProject> findByIdStudentID(@NonNull String studentID);
    List<StudentProject> findByIdProjectIdentifier(@NonNull String projectID);
    StudentProject findByIdProjectIdentifierAndIdStudentID(@NonNull String projectID, @NonNull String studentID);
    List<StudentProject> findByBestGrade(String grade);
}