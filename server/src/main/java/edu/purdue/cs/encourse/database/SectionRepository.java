package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Section;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SectionRepository extends CrudRepository<Section, String> {
    Section findBySectionIdentifier(@NonNull String sectionID);
    List<Section> findByCourseID(@NonNull String courseID);
    List<Section> findBySemesterAndCourseID(@NonNull String semester, @NonNull String courseID);
    List<Section> findAll();
    boolean existsBySectionIdentifier(@NonNull String sectionID);
}