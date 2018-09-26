package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.ProfessorCourse;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProfessorCourseRepository extends CrudRepository<ProfessorCourse, String> {
    List<ProfessorCourse> findByIdProfessorID(@NonNull String professorID);
    List<ProfessorCourse> findByIdCourseID(@NonNull String courseID);
    List<ProfessorCourse> findByIdSemester(@NonNull String semester);
}
