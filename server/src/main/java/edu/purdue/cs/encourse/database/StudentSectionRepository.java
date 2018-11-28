package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.StudentSection;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentSectionRepository extends CrudRepository<StudentSection, String> {
    List<StudentSection> findByIdStudentID(@NonNull String studentID);
    List<StudentSection> findByIdSectionID(@NonNull String sectionID);
    StudentSection findByIdStudentIDAndIdSectionID(@NonNull String studentID, @NonNull String sectionID);
    boolean existsByIdStudentID(@NonNull String studentID);
    boolean existsByIdSectionID(@NonNull String sectionID);
}
