package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    List<Authority> findAll();
    Authority findDistinctByName(String name);
}
