package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.StudentSection;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentSectionRepository extends CrudRepository<StudentSection, String> {
    List<StudentSection> findByIdStudentID(@NonNull String studentID);
    List<StudentSection> findByIdSectionIdentifier(@NonNull String sectionID);
    boolean existsByIdStudentID(@NonNull String studentID);
    boolean existsByIdSectionIdentifier(@NonNull String sectionID);
}
