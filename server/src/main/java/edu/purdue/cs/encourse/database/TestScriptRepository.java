package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.TestScript;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Killian Le Clainche on 1/18/2019.
 */
public interface TestScriptRepository extends JpaRepository<TestScript, Long> {
	
}
