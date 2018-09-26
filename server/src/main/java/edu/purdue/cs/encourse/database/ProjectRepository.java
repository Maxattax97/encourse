package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Project;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<Project, String> {
    Project findByProjectIdentifier(@NonNull String projectID);
    boolean existsByProjectIdentifier(@NonNull String projectID);
}