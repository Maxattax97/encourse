package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Student;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, String> {
    Student findByUserID(@NonNull String userID);
    Student findByUserName(@NonNull String userName);
    boolean existsByUserID(@NonNull String userID);
    boolean existsByUserName(@NonNull String userName);
}
