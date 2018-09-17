package edu.purdue.cs.encourse.database;

import lombok.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface TeachingAssistantRepository extends CrudRepository<TeachingAssistant, Long> {
    TeachingAssistant findByUserID(@NonNull String UID);
    boolean existsByUserID(@NonNull String UID);
}
