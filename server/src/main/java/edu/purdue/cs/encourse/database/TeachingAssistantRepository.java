package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.TeachingAssistant;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

public interface TeachingAssistantRepository extends CrudRepository<TeachingAssistant, String> {
    TeachingAssistant findByUserID(@NonNull String userID);
    TeachingAssistant findByUserName(@NonNull String userName);
    boolean existsByUserID(@NonNull String userID);
    boolean existsByUserName(@NonNull String userName);
}
