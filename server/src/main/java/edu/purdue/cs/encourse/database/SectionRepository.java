package edu.purdue.cs.encourse.database;

import lombok.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SectionRepository extends CrudRepository<Section, String> {
    Section findBySectionIdentifier(@NonNull String sectionID);
    List<Section> findByCourseID(@NonNull String courseID);
    boolean existsBySectionIdentifier(@NonNull String sectionID);
}