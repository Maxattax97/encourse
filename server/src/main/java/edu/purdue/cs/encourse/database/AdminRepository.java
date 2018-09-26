package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.CollegeAdmin;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends CrudRepository<CollegeAdmin, Long> {
    CollegeAdmin findByUserID(@NonNull String userID);
    CollegeAdmin findByUserName(@NonNull String userName);
    boolean existsByUserID(@NonNull String userID);
}
