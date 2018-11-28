package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.StudentProjectDate;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentProjectDateRepository extends CrudRepository<StudentProjectDate, String> {
    List<StudentProjectDate> findByIdStudentID(@NonNull String studentID);
    List<StudentProjectDate> findByIdProjectID(@NonNull String projectID);
    List<StudentProjectDate> findByIdDate(@NonNull String date);
    List<StudentProjectDate> findByIdDateAndIdStudentID(@NonNull String date, @NonNull String studentID);
    List<StudentProjectDate> findByIdProjectIDAndIdStudentID(@NonNull String projectID, @NonNull String studentID);
    List<StudentProjectDate> findByIdDateAndIdProjectID(@NonNull String date, @NonNull String projectID);
    StudentProjectDate findByIdDateAndIdProjectIDAndIdStudentID(@NonNull String date, @NonNull String projectID, @NonNull String studentID);
}