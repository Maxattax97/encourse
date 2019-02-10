package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.StudentComparison;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Killian Le Clainche on 2/10/2019.
 */
public interface StudentComparisonRepository extends JpaRepository<StudentComparison, Long> {
	
}
