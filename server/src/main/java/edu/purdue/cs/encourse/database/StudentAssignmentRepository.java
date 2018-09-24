package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.StudentAssignment;
import lombok.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentAssignmentRepository extends CrudRepository<StudentAssignment, Long> {
    List<StudentAssignment> findByIdTeachingAssistantID(String teachingAssistantID);
    List<StudentAssignment> findByIdStudentID(@NonNull String studentID);
    List<StudentAssignment> findByIdSectionIdentifier(@NonNull String sectionID);
}
