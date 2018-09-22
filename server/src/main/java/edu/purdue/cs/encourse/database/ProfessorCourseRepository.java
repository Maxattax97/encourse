package edu.purdue.cs.encourse.database;

import edu.purdue.cs.encourse.domain.relations.ProfessorCourse;
import lombok.*;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProfessorCourseRepository extends CrudRepository<ProfessorCourse, Long> {
    List<ProfessorCourse> findByIdProfessorID(@NonNull String professorID);
    List<ProfessorCourse> findByIdCourseID(String courseID);
    List<ProfessorCourse> findByIdSemester(@NonNull String studentID);
}
