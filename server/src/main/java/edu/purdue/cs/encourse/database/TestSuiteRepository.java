package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.TestSuite;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Killian Le Clainche on 1/18/2019.
 */
public interface TestSuiteRepository extends JpaRepository<TestSuite, Long> {
	
}
