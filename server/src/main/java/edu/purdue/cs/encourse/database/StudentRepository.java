package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Student;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Long> {
}
