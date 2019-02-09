package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.Course;
import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Killian Le Clainche on 2/1/2019.
 */
public interface CourseStudentRepository extends JpaRepository<CourseStudent, Long> {
	
}
