package edu.purdue.cs.encourse.database;

import lombok.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface AdminRepository extends CrudRepository<CollegeAdmin, Long> {
    CollegeAdmin findByUserID(@NonNull String UID);
    CollegeAdmin findByUserName(@NonNull String userName);
    boolean existsByUserID(@NonNull String UID);
}
