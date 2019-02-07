package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Professor;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
}
