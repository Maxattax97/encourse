package edu.purdue.cs.encourse.database;

import lombok.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface StudentRepository extends CrudRepository<Student, Long> {
    Student findByUserID(@NonNull String userID);
    Student findByUserName(@NonNull String userName);
    boolean existsByUserID(@NonNull String userID);
}
