package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.StudentComparison;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Killian Le Clainche on 2/10/2019.
 */
public interface StudentComparisonRepository extends JpaRepository<StudentComparison, Long> {
	
	List<StudentComparison> findAllByStudentProject1_IdInOrStudentProject2_IdIn(@NonNull List<Long> studentIds1, @NonNull List<Long> studentIds2);
	
}
