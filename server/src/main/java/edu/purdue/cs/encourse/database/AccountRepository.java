package edu.purdue.cs.encourse.database;

import lombok.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByUserID(@NonNull String UID);
    Account findByUserName(@NonNull String userName);
    boolean existsByUserID(@NonNull String UID);
    List<Account> findAll();
}