package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.TestSuite;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Killian Le Clainche on 1/18/2019.
 */
public interface TestSuiteRepository extends JpaRepository<TestSuite, Long> {
	
	boolean existsByNameEqualsAndProject_ProjectID(@NonNull String name, @NonNull Long projectID);
	
}
