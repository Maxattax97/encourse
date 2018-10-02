package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Account;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, String> {
    Account findByUserID(@NonNull String userID);
    Account findByUserName(@NonNull String userName);
    boolean existsByUserID(@NonNull String userID);
    boolean existsByUserName(@NonNull String userName);
    List<Account> findAll();
}