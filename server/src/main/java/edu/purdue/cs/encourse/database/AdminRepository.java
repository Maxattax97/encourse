package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.CollegeAdmin;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends JpaRepository<CollegeAdmin, Long> {
}
