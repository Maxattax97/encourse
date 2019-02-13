package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Project;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    @Query("select p from Project p where p.dueDate >= p.analyzeDateTime")
    List<Project> findAllProjectsByAnalyzeDate();

    boolean existsByName(@NonNull String name);
}