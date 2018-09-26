package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Professor;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

public interface ProfessorRepository extends CrudRepository<Professor, Long> {
    Professor findByUserID(@NonNull String userID);
    Professor findByUserName(@NonNull String userName);
    boolean existsByUserID(@NonNull String userID);
    boolean existsByUserName(@NonNull String userName);
}
