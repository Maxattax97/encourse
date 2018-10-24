package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.TeachingAssistantSection;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeachingAssistantSectionRepository extends CrudRepository<TeachingAssistantSection, String> {
    List<TeachingAssistantSection> findByIdTeachingAssistantID(@NonNull String teachingAssistantID);
    List<TeachingAssistantSection> findByIdSectionID(@NonNull String sectionID);
    TeachingAssistantSection findByIdTeachingAssistantIDAndIdSectionID(@NonNull String teachingAssistantID, @NonNull String sectionID);
}