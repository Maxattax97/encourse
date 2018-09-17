package edu.purdue.cs.encourse.database;

import lombok.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface ProjectRepository extends CrudRepository<Project, Long> {
    Project findByProjectIdentifier(@NonNull String projectID);
}