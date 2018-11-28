package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.StudentProjectTest;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentProjectTestRepository extends CrudRepository<StudentProjectTest, String> {
    List<StudentProjectTest> findByIdProjectID(@NonNull String projectID);
    List<StudentProjectTest> findByIdProjectIDAndIdStudentIDAndIsHidden(@NonNull String projectID, @NonNull String studentID, boolean isHidden);
    StudentProjectTest findByIdProjectIDAndIdTestScriptNameAndIdStudentID(@NonNull String projectID, @NonNull String testScriptName, @NonNull String studentID);
}