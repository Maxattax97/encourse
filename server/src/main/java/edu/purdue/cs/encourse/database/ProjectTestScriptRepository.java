package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.ProjectTestScript;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectTestScriptRepository extends CrudRepository<ProjectTestScript, String> {
    List<ProjectTestScript> findByIdProjectIdentifier(@NonNull String projectID);
    List<ProjectTestScript> findByIdTestScriptName(@NonNull String testScriptName);
}