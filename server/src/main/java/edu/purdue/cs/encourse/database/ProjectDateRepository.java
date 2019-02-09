package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Project;
import edu.purdue.cs.encourse.domain.ProjectDate;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Killian Le Clainche on 1/16/2019.
 */
public interface ProjectDateRepository extends JpaRepository<ProjectDate, Long> {
	
	List<ProjectDate> findAllByProjectAndDateGreaterThanEqual(@NonNull Project project, @NonNull LocalDate date);
	
	ProjectDate findFirstByProjectAndDate(@NonNull Project project, @NonNull LocalDate date);
	
}
