package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Course;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Killian Le Clainche on 1/18/2019.
 */
public interface CourseRepository extends JpaRepository<Course, Long> {
	Course findByNameAndSemester(@NonNull String name, @NonNull String semester);
}
