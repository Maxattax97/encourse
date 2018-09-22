package edu.purdue.cs.encourse.database;

import lombok.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByUserID(@NonNull String userID);
    Account findByUserName(@NonNull String userName);
    boolean existsByUserID(@NonNull String userID);
}