package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.AdditionHash;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Killian Le Clainche on 1/20/2019.
 */
public interface AdditionHashRepository extends JpaRepository<AdditionHash, String> {
	
}
