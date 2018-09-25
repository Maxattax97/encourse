package edu.purdue.cs.encourse.database;

import lombok.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface TeachingAssistantRepository extends CrudRepository<TeachingAssistant, Long> {
    TeachingAssistant findByUserID(@NonNull String userID);
    TeachingAssistant findByUserName(@NonNull String userName);
    boolean existsByUserID(@NonNull String userID);
    boolean existsByUserName(@NonNull String userName);
}
