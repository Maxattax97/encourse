package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.CourseStudent;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * Created by Killian Le Clainche on 1/15/2019.
 */
public interface StudentCourseRepository extends JpaRepository<CourseStudent, String> {
	List<CourseStudent> findByCourseIDAndSemester(@NonNull String courseID, @NonNull String semester);
	boolean existsByCourseIDAndSemesterAndUserID(@NonNull String courseID, @NonNull String semester, @NonNull String userID);
}
