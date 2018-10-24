package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.TeachingAssistantCourse;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeachingAssistantCourseRepository extends CrudRepository<TeachingAssistantCourse, String> {
    List<TeachingAssistantCourse> findByIdTeachingAssistantID(@NonNull String teachingAssistantID);
    List<TeachingAssistantCourse> findByIdCourseID(@NonNull String courseID);
    List<TeachingAssistantCourse> findByIdSemester(@NonNull String semester);
    List<TeachingAssistantCourse> findByIdSemesterAndIdCourseID(@NonNull String semester, @NonNull String courseID);
    TeachingAssistantCourse findByIdTeachingAssistantIDAndIdSemesterAndIdCourseID(@NonNull String teachingAssistantID, @NonNull String semester, @NonNull String courseID);
}