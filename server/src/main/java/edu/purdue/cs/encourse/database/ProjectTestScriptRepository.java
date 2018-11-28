package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.ProjectTestScript;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectTestScriptRepository extends CrudRepository<ProjectTestScript, String> {
    List<ProjectTestScript> findByIdProjectID(@NonNull String projectID);
    List<ProjectTestScript> findByIdProjectIDAndIsHidden(@NonNull String projectID, boolean isHidden);
    List<ProjectTestScript> findByIdTestScriptName(@NonNull String testScriptName);
    ProjectTestScript findByIdProjectIDAndIdTestScriptName(@NonNull String projectID, @NonNull String testScriptName);
}