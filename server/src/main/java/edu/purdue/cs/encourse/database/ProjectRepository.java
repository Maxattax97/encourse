package edu.purdue.cs.encourse.database;

import lombok.*;
import edu.purdue.cs.encourse.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface ProjectRepository extends CrudRepository<Project, String> {
    Project findByProjectIdentifier(@NonNull String projectID);
    boolean existsByProjectIdentifier(@NonNull String projectID);
}