package edu.purdue.cs.encourse.database;

import lombok.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByUserID(@NonNull String userID);
    Account findByUserName(@NonNull String userName);
<<<<<<< HEAD
    boolean existsByUserID(@NonNull String userID);
=======
    boolean existsByUserID(@NonNull String UID);
    List<Account> findAll();
>>>>>>> feature/auth
}