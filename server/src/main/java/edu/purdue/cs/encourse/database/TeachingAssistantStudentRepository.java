package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.TeachingAssistantStudent;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeachingAssistantStudentRepository extends CrudRepository<TeachingAssistantStudent, String> {
    List<TeachingAssistantStudent> findByIdTeachingAssistantID(@NonNull String teachingAssistantID);
    List<TeachingAssistantStudent> findByIdTeachingAssistantIDAndIdCourseID(@NonNull String teachingAssistantID, @NonNull String courseID);
    List<TeachingAssistantStudent> findByIdStudentID(@NonNull String studentID);
    TeachingAssistantStudent findByIdTeachingAssistantIDAndIdStudentIDAndIdCourseID(@NonNull String teachingAssistantID, @NonNull String studentID, @NonNull String courseID);
}