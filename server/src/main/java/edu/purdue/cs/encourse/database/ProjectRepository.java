package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Project;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, String> {
    Project findByProjectIdentifier(@NonNull String projectID);
    List<Project> findBySemesterAndCourseID(@NonNull String semester, @NonNull String courseID);
    boolean existsByProjectIdentifier(@NonNull String projectID);
}