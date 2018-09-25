package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.TeachingAssistantStudent;
import lombok.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeachingAssistantStudentRepository extends CrudRepository<TeachingAssistantStudent, Long> {
    List<TeachingAssistantStudent> findByIdTeachingAssistantID(@NonNull String teachingAssistantID);
    List<TeachingAssistantStudent> findByIdStudentID(@NonNull String studentID);
}