package edu.purdue.cs.encourse.database;

import lombok.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface SectionRepository extends CrudRepository<Section, Long> {
    Section findBySectionIdentifier(@NonNull String sectionID);
    boolean existsBySectionIdentifier(@NonNull String sectionID);
}