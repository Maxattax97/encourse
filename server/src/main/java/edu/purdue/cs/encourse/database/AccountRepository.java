package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}